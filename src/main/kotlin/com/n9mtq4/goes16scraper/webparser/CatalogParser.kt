package com.n9mtq4.goes16scraper.webparser

import com.n9mtq4.goes16scraper.CONNECTION_TIMEOUT_MS
import com.n9mtq4.goes16scraper.ImageOptions
import com.n9mtq4.goes16scraper.ImageToDownload
import com.n9mtq4.goes16scraper.ImageToDownloadList
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.jsoup.Jsoup

/**
 * Created by will on 4/25/18 at 3:25 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

/**
 * Gets a list of images from the image catalog
 * 
 * It seems like catalog.json is no longer generated and
 * instead they are embedding the list into javascript that is
 * served inside the php page for animations (animationImages).
 * 
 * That means that this no longer works, but will remain for
 * historical reasons.
 * */
internal fun parseCatalog(imageOptions: ImageOptions): ImageToDownloadList {
	
	val urlStr = getNoaaUrlStr(imageOptions)
	
	val jsonUrl = urlStr + "catalog.json"
	println(jsonUrl)
	
	val jsonStr = Jsoup
		.connect(jsonUrl)
		.header("Accept-Encoding", "gzip, deflate, br")
		.userAgent(USER_AGENT)
		.ignoreContentType(true)
		.timeout(CONNECTION_TIMEOUT_MS)
		.maxBodySize(0)
		.ignoreHttpErrors(true)
		.followRedirects(true)
		.execute()
		.body()
	
	val parser = JSONParser()
	val json: JSONObject = parser.parse(jsonStr) as JSONObject
	val images = json["images"] as JSONObject
	val imageList = (images[imageOptions.res] as JSONArray).toList().map { it as String }
	val imageUrlList = imageList.map { ImageToDownload(it, urlStr + it) }
	
	return imageUrlList
	
}
