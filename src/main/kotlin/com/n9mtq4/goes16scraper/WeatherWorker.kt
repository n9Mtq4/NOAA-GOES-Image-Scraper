package com.n9mtq4.goes16scraper

import kotlinx.coroutines.experimental.runBlocking

/**
 * Created by will on 12/22/2017 at 7:19 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
class WeatherWorker(val sleepTime: Long, val checkSleepTime: Long) : Runnable {
	
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
	
	fun work() {
		
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
	
	private fun runCycle() = runBlocking { 
		
		
	}
	
	private suspend fun downloadImage() {
		
	}
	
}
