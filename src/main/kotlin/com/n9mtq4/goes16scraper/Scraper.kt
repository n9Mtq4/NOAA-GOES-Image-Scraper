package com.n9mtq4.goes16scraper

import com.n9mtq4.goes16scraper.utils.readFromJar
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.GnuParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import java.io.File

/**
 * Created by will on 12/22/2017 at 7:16 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

fun main(args: Array<String>) {
	
	// the options
	val options = Options().apply {
		
		addOption("o", "output", true, "selects the output directory for the images")
		addOption("t", "type", true, "the type of image (run --types for list of types)")
		addOption("r", "resolution", true, "selects the image resolution to download (run --resolutions for list of resolutions)")
		addOption("b", "band", true, "selects the color/band  (run --bands for list of types)")
		addOption(null, "sleeptime", true, "the time between downloading images")
		addOption(null, "checksleeptime", true, "the time between checking if sleep time has passed")
		addOption(null, "infotechnique", true, "the strategy for gaining information on images")
		addOption(null, "types", false, "prints a list of types")
		addOption(null, "resolutions", false, "prints a list of resolutions")
		addOption(null, "bands", false, "prints a list of bands")
		addOption(null, "infotechniques", false, "prints a list of strategies for gaining information on images")
		addOption(null, "help", false, "prints this help message")
		
	}
	
	// the parser
	val parser: CommandLineParser = GnuParser()
	val cliargs = parser.parse(options, args)
	
	// help information
	if (cliargs.hasOption("help")) {
		val helpFormatter = HelpFormatter()
		helpFormatter.printHelp("java -jar jarName.jar [OPTIONS]", options)
		return
	}
	// lists of things
	val helpList = listOf("types", "resolutions", "bands", "infotechniques")
	helpList
			.filter { cliargs.hasOption(it) }
			.map { readFromJar("/text/$it.txt") }
			.onEach(::println)
			.forEach { return }
	
	// get command line args or default values
	val outputDir = cliargs.getOptionValue("output") ?: DEFAULT_OUTPUT_DIRECTORY
	val type = cliargs.getOptionValue("types") ?: DEFAULT_TYPE
	val res = cliargs.getOptionValue("resolution") ?: DEFAULT_RESOLUTION
	val band = cliargs.getOptionValue("band") ?: DEFAULT_BAND
	val infoTechnique = cliargs.getOptionValue("infotechnique") ?: DEFAULT_INFOTECHNIQUE
	val sleepTime = cliargs.getOptionValue("sleeptime")?.toLong() ?: DEFAULT_SLEEP_TIME
	val checkSleepTime = cliargs.getOptionValue("checksleeptime")?.toLong() ?: DEFAULT_CHECK_SLEEP_TIME
	
	val imageOptions = ImageOptions(File(outputDir), type, res, band, infoTechnique)
	
	// start a weather worker with the options
	val weatherWorker = WeatherWorker(sleepTime, checkSleepTime, imageOptions)
	weatherWorker.run()
	
}