package com.n9mtq4.goes16scraper.webparser

import com.n9mtq4.goes16scraper.ImageOptions
import com.n9mtq4.goes16scraper.ImageToDownload
import com.n9mtq4.goes16scraper.ImageToDownloadList
import com.n9mtq4.goes16scraper.TIMEOUT_MS
import org.jsoup.Jsoup

/**
 * Created by will on 4/25/18 at 4:00 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

private const val FULL_LIST_SELECTOR = "body > pre"
private const val SPACE_REGEX_STRING = "\\s+"

internal fun parseDirectoryListSize(imageOptions: ImageOptions): ImageToDownloadList {
	
	val urlStr = "$ROOT_URL${imageOptions.type}/${imageOptions.band}/"
	
	val dListDom = Jsoup
			.connect(urlStr)
			.header("Accept-Encoding", "gzip, deflate, br")
			.userAgent(USER_AGENT)
			.timeout(TIMEOUT_MS)
			.maxBodySize(0)
			.ignoreHttpErrors(true)
			.followRedirects(true)
			.get()
	
	val spaceRegex = Regex(SPACE_REGEX_STRING)
	
	val imgUrlSizeList = dListDom
			.select(FULL_LIST_SELECTOR)
			.text()
			.split("\n")
			.map { it.trim() }
			.map { it.split(spaceRegex) }
			.filter { it.size == 4 }
			.map { it[0] to it[3].toLong() }
			.filter { (name, _) -> imageOptions.res in name }
			.filter { (name, _) -> "GOES16" in name }
			.map { (name, size) -> ImageToDownload(name, urlStr + name, size) }
	
	return imgUrlSizeList
	
}
