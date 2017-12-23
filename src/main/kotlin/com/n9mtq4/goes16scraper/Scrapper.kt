package com.n9mtq4.goes16scraper

import com.n9mtq4.goes16scraper.utils.readFromJar
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.GnuParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options

/**
 * Created by will on 12/22/2017 at 7:16 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

fun main(args: Array<String>) {
	
	// the options
	val options = Options().apply {
		
		addOption("o", "output", true, "selects the output directory for the images")
		addOption("r", "resolution", true, "selects the image resolution to download (run --resolutions for list of resolutions)")
		addOption("b", "band", true, "selects the color/band (1-16 or geocolor)")
		addOption(null, "sleeptime", true, "the time between downloading images")
		addOption(null, "checksleeptime", true, "the time between checking if sleep time has passed")
		addOption(null, "resolutions", false, "prints a list of resolutions")
		addOption(null, "bands", false, "prints a list of bands")
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
	// resolutions
	if (cliargs.hasOption("resolutions")) {
		readFromJar("/text/resolutions.txt").run(::println)
	}
	// bands
	if (cliargs.hasOption("bands")) {
		readFromJar("/text/bands.txt").run(::println)
	}
	
	// get command line args or default values
	val outputDir = cliargs.getOptionValue("output") ?: DEFAULT_OUTPUT_DIRECTORY
	val res = cliargs.getOptionValue("resolution") ?: DEFAULT_RESOLUTION
	val band = cliargs.getOptionValue("band") ?: DEFAULT_BAND
	val sleepTime = cliargs.getOptionValue("sleeptime")?.toLong() ?: DEFAULT_SLEEP_TIME
	val checkSleepTime = cliargs.getOptionValue("checksleeptime")?.toLong() ?: DEFAULT_CHECK_SLEEP_TIME
	
	// start a weather worker with the options
	
}
