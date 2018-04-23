package com.n9mtq4.goes16scraper

import com.n9mtq4.goes16scraper.utils.getTimestamp
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by will on 12/22/2017 at 7:19 PM.
 * 
 * @author Will "n9Mtq4" Bresnahan
 */

const val ROOT_URL = "https://cdn.star.nesdis.noaa.gov/GOES16/ABI/"
const val USER_AGENT = "n9Mtq4-goes-east-scrapper/0.2 (+https://github.com/n9Mtq4/NOAA-Goes-16-image-scraper)"

class WeatherWorker(private val sleepTime: Long, private val checkSleepTime: Long, private val imageOptions: ImageOptions) : Runnable {
	
	var ticks = 0
	var running = true
	var targetTime = System.currentTimeMillis()
	
	override fun run() {
		
		while (running) {
			
//			spin lock for time
//			thread.sleep doesn't stay consistent against computer sleeping
//			ex: Thread.sleep(1000 * 60 * 60) should sleep for a min
//			if the computer is put to sleep in the middle of that, it will be longer
//			this spin lock will fix that
			while (running) {
				val currentTime = System.currentTimeMillis()
				if (targetTime - currentTime < checkSleepTime / 2) break
				Thread.sleep(checkSleepTime) // sleep for a couple of minutes
			}
			
//			update ticks
			ticks++
			
//			download all the images
			println("Started download: #$ticks at ${getTimestamp()}")
			work()
			println("Finished download #$ticks at ${getTimestamp()}")
			println("The next download is targeted for ${getTimestamp(sleepTime)}")
			
//			update target time
			targetTime = System.currentTimeMillis() + sleepTime
			
		}
		
	}
	
	private fun work() {
		
		checkFilePermissions()
		
		try {
			
			// generate main directory url
			val type = imageOptions.type
			val band = imageOptions.band.run { 
				if (toIntOrNull() == null) {
					this
				}else {
					if (length < 1) "0$this" else this
				}
			}.toUpperCase() // if its a band #, make sure it is two chars
			
			val urlStr = "$ROOT_URL$type/$band/"
			val jsonUrl = urlStr + "catalog.json"
			println(jsonUrl)
			
			val jsonStr = Jsoup
					.connect(jsonUrl)
					.header("Accept-Encoding", "gzip, deflate, br")
					.userAgent(USER_AGENT)
					.ignoreContentType(true)
					.timeout(3000)
					.maxBodySize(0).ignoreHttpErrors(true).followRedirects(true)
					.execute()
					.body()
			
			val parser = JSONParser()
			val json: JSONObject = parser.parse(jsonStr) as JSONObject
			val images = json["images"] as JSONObject
			val imageList = (images[imageOptions.res] as JSONArray).toList().map { it as String }
			
			println(imageList)
			
			val imageUrlList = imageList.map { it to urlStr + it }
			
			downloadAll(imageUrlList)
			
			
		} catch (e: Exception) {
			println("Error downloading the images! Will try again at ${getTimestamp(sleepTime)}. (${e.localizedMessage})")
			e.printStackTrace()
		}
		
	}
	
	private fun downloadAll(imageUrls: List<Pair<String, String>>) { 
		
		val totalSize = imageUrls.size
		val failed = AtomicInteger(0)
		val alreadyDownloaded = AtomicInteger(0)
		val succeeded = AtomicInteger(0)
		
		// download in asynchronously in groups of 5
		imageUrls.divideIntoGroupsOf(5).forEach { imgBatchGroup ->
			
			runBlocking { 
				
				imgBatchGroup.map { (name, url) -> launch {
					try {
						downloadImage(name, url)
						succeeded.incrementAndGet()
					}catch (e: AlreadyDownloadedException) {
						alreadyDownloaded.incrementAndGet()
					}catch (e: Exception) {
						failed.incrementAndGet()
					}
				} }.forEach { it.join() }
				
			}
			
		}
		
		println("New: $succeeded, AlreadyHad: $alreadyDownloaded, Failed: $failed, Total: $totalSize")
		
	}
	
	private fun <R> List<R>.divideIntoGroupsOf(size: Int): List<List<R>> {
		
		var l = this
		val o = ArrayList<List<R>>()
		while (l.isNotEmpty()) {
			val s = if (l.size < size) l.size else size
			o.add(l.take(s))
			l = l.drop(s)
		}
		return o.toList()
		
	}
	
	/**
	 * checks to make sure that the file system is allowing us to read and write the required directories
	 * the working directory must be rw for detecting and possibly creating a new directory
	 * the ./img/ directory must be rw for checking if images exist and downloading images
	 * */
	private fun checkFilePermissions() {
		
		// make sure we can create the output directory to put the images
		imageOptions.outputDir.absoluteFile.parentFile.run {
			if (!canRead()) println("This program can't read the current directory. Check your permissions.")
			if (!canWrite()) println("This program can't create the required directory! Check your permissions.")
		}
		
		// make the output directory if needed
		if (!imageOptions.outputDir.exists()) imageOptions.outputDir.mkdirs()
		
		// make sure we can read and write in the output directory
		imageOptions.outputDir.run {
			if (!canRead()) println("This program can't read the current directory. Check your permissions.")
			if (!canWrite()) println("This program can't create the required directory! Check your permissions.")
		}
		
	}
	
	/**
	 * Note: this method may take anywhere from 0 to the CHECK_SLEEP_TIME
	 * to register and stop the run method's loop
	 * THIS DOES NOT STOP THE THREAD, ONLY STOPS THE RUN METHOD
	 * THE IMAGES WILL BE DOWNLOADED ONE MORE TIME BEFORE THE RUN METHOD STOPS
	 * */
	fun stop() {
		this.running = false
	}
	
	private fun downloadImage(imageName: String, imageUrl: String) {
		
		val targetFile = File(imageOptions.outputDir, imageName)
		if (targetFile.exists()) throw AlreadyDownloadedException(imageUrl)
		
		val url = URL(imageUrl)
		val urlConnection = url.openConnection()
		urlConnection.setRequestProperty("User-Agent", USER_AGENT)
		val rbc = Channels.newChannel(urlConnection.getInputStream())
		val fos = FileOutputStream(targetFile)
		fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
		fos.close()
		
	}
	
}
