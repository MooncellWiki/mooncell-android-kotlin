package wiki.fgo.app.network

import android.util.Log
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

class ContentHandler : DefaultHandler() {

    var nodeName: String? = null
    var id: StringBuilder? = null
    var name: StringBuilder? = null
    var version: StringBuilder? = null


    @Throws(SAXException::class)
    override fun startDocument() {
        id = StringBuilder()
        name = StringBuilder()
        version = StringBuilder()
    }


    @Throws(SAXException::class)
    override fun endDocument() {
        super.endDocument()
    }


    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        nodeName = localName
    }


    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        when (nodeName) {
            "id" -> {
                id?.append(ch,start,length)
            }
            "name" -> {
                name?.append(ch,start,length)
            }
            "version" -> {
                version?.append(ch, start, length)
            }
        }
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        if("app" == localName) {
            Log.d("ContentHandler","id is :" + id?.toString()?.trim())
            Log.d("ContentHandler","name is :" + name?.toString()?.trim())
            Log.d("ContentHandler","version is :" + version?.toString()?.trim())

            id?.setLength(0)
            name?.setLength(0)
            version?.setLength(0)
        }
    }

}