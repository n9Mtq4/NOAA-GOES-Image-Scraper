package com.n9mtq4.goes16scraper

import com.n9mtq4.goes16scraper.utils.getTimestamp
import com.n9mtq4.goes16scraper.webparser.USER_AGENT
import com.n9mtq4.goes16scraper.webparser.parseCatalog
import com.n9mtq4.goes16scraper.webparser.parseDirectoryList
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
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
			
			// make sure that everything is good
			imageOptions.sanitize()
			
			val imageUrlList = when(imageOptions.infoTechnique) {
				"catalog" -> parseCatalog(imageOptions)
				"directorylist" -> parseDirectoryList(imageOptions)
				else -> {
					println("Not a valid info technique")
					return
				}
			}
			
			downloadAll(imageUrlList)
			
			
		} catch (e: Exception) {
			println("Error downloading the images! Will try again at ${getTimestamp(sleepTime)}. (${e.localizedMessage})")
			e.printStackTrace()
		}
		
	}
	
	private fun downloadAll(imageUrls: List<Pair<String, String>>) { 
		
		val totalSize = imageUrls.size
		val failed = AtomicInteger(0)
		
		val imagesToDownload = imageUrls.filter { (name, _) -> shouldDownloadImage(name) }
		
		// download in asynchronously in groups of 4
		// there are 4 every hour, so this is a nice number
		imagesToDownload.divideIntoGroupsOf(4).forEach { imgBatchGroup ->
			
			runBlocking {
				
				imgBatchGroup.map { (name, url) -> launch {
					try {
						downloadImage(name, url)
					}catch (e: Exception) {
						failed.incrementAndGet()
					}
				} }.forEach { it.join() }
				
			}
			
		}
		
		// calculate some stats
		val alreadyDownloaded = totalSize - imagesToDownload.size
		val succeeded = imagesToDownload.size - failed.toInt()
		
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
	
	/**
	 * Checks to see if the image with the specified name should
	 * be downloaded.
	 * 
	 * If the image exists (has already been downloaded) it should
	 * not be downloaded again
	 * 
	 * @param imageName the name of the image to check
	 * @return true if the image should be downloaded
	 * */
	private fun shouldDownloadImage(imageName: String): Boolean {
		
		val targetFile = getTargetImageFile(imageName)
		
		// TODO: could check for abnormal file sizes here to detect errors
		return !targetFile.exists()
		
	}
	
	private fun downloadImage(imageName: String, imageUrl: String) {
		
		val targetFile = getTargetImageFile(imageName)
		
		println("Downloading $imageName")
		
		val url = URL(imageUrl)
		val urlConnection = url.openConnection()
		urlConnection.setRequestProperty("User-Agent", USER_AGENT)
		val rbc = Channels.newChannel(urlConnection.getInputStream())
		val fos = FileOutputStream(targetFile)
		fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
		fos.close()
		
	}
	
	private fun getTargetImageFile(imageName: String): File = File(imageOptions.outputDir, imageName)
	
}
