package com.n9mtq4.goes16scraper

/**
 * Created by will on 4/22/18 at 9:06 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
class AlreadyDownloadedException(val url: String) : Exception(url)
