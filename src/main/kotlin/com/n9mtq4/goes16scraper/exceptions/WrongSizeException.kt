package com.n9mtq4.goes16scraper.exceptions

/**
 * Created by will on 7/7/18 at 11:01 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
class WrongSizeException(imageName: String, expectedSize: Long, actualSize: Long) : Exception("$imageName file size is wrong. Expected $expectedSize, got: $actualSize")
