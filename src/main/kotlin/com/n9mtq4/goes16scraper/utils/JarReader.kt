package com.n9mtq4.goes16scraper.utils

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by will on 12/22/2017 at 7:44 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

/**
 * Reads the text from a file inside this jar
 *
 * @param path the path inside the jar. Make sure it starts with a '/'
 * @return a string of the resources text contents
 * */
internal fun readFromJar(path: String): String {
	
	val input = object {}::class.java.getResourceAsStream(path)
	val ir = InputStreamReader(input)
	val br = BufferedReader(ir)
	
	val text = br.readLines().joinToString(separator = "\n")
	
	br.close()
	ir.close()
	input.close()
	
	return text
	
}
