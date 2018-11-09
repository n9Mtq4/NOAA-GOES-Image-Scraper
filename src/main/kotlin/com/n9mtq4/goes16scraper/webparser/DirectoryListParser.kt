package com.n9mtq4.goes16scraper.webparser

import com.n9mtq4.goes16scraper.CONNECTION_TIMEOUT_MS
import com.n9mtq4.goes16scraper.ImageOptions
import com.n9mtq4.goes16scraper.ImageToDownload
import com.n9mtq4.goes16scraper.ImageToDownloadList
import org.jsoup.Jsoup

/**
 * Created by will on 4/25/18 at 4:00 PM.
 * 
 * @author Will "n9Mtq4" Bresnahan
 */

private const val LINK_SELECTOR = "body > pre > a"

internal fun parseDirectoryList(imageOptions: ImageOptions): ImageToDownloadList {
	
	val urlStr = "$ROOT_URL${imageOptions.type}/${imageOptions.band}/"
	
	val dListDom = Jsoup
		.connect(urlStr)
		.header("Accept-Encoding", "gzip, deflate, br")
		.userAgent(USER_AGENT)
		.timeout(CONNECTION_TIMEOUT_MS)
		.maxBodySize(0)
		.ignoreHttpErrors(true)
		.followRedirects(true)
		.get()
	
	val imgUrlList = dListDom
		.select(LINK_SELECTOR)
		.map { it.text() }
		.filter { imageOptions.res in it }
		.filter { "GOES16" in it }
		.map { ImageToDownload(it, urlStr + it) }
	
	return imgUrlList
	
}
