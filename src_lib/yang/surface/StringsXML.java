package yang.surface;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import yang.graphics.font.DrawableAnchoredLines;

//TODO xml comments not working
public class StringsXML {

	public static final String UNKNOWN_KEY = "NONE";
	
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	private static final String RESOURCES_OPEN = "<resources>";
	private static final String RESOURCES_CLOSE = "</resources>";
	
	private final HashMap<String, String> mStrings;
	private StringsXML mFallbackStringsXML = null;

	StringsXML() {
		mStrings = new HashMap<String, String>();
	}

	public StringsXML(InputStream xmlStream,StringsXML fallbackStrings) {
		this();
		mFallbackStringsXML = fallbackStrings;
		load(xmlStream);
	}

	public StringsXML(InputStream xmlStream) {
		this(xmlStream,null);
	}

	public StringsXML(StringsXML fallbackStrings) {
		this();
		mFallbackStringsXML = fallbackStrings;
	}

	public StringsXML(boolean createFallback) {
		mStrings = new HashMap<String, String>();
		
		if(createFallback) {
			mFallbackStringsXML = new StringsXML();
		}
	}

	public StringsXML load(InputStream xmlStream) {
		return load(xmlStream, this);
	}
	
	public StringsXML loadFallback(InputStream xmlStream) {
		return load(xmlStream, mFallbackStringsXML);
	}
	
	private final static StringsXML load(InputStream xmlStream, StringsXML stringsXML) {
		stringsXML.mStrings.clear();
		try {
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			final DocumentBuilder db = dbf.newDocumentBuilder();
			final Document doc = db.parse(xmlStream);

			final Element root = doc.getDocumentElement();
			final NodeList strings = root.getElementsByTagName("string");
			final int amount = strings.getLength();

			for (int i = 0; i < amount; ++i) {
				final Node node = strings.item(i);
				final String string = node.getTextContent().trim().replace("\n", "\0").replace("\t", "\0").replace("\\n", "\n").replace("\\t", "\t").replace("\\'", "'");
				final String key = node.getAttributes().getNamedItem("name").getTextContent();
				stringsXML.mStrings.put(key, string);
			}

			xmlStream.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		
		return stringsXML;
	}

	public String getRawString(String name) {
		final String toReturn = mStrings.get(name);
		if (toReturn == null) {
			if(mFallbackStringsXML==null)
				System.err.println("string:" +name +" not found");
			else
				return mFallbackStringsXML.getRawString(name);
			return UNKNOWN_KEY;
		}
		else return toReturn;
	}

	public DrawableAnchoredLines createDrawableString(String name,float horizontalAnchor) {
		return (DrawableAnchoredLines)new DrawableAnchoredLines(getRawString(name)).setHorizontalAnchor(horizontalAnchor).setConstant();
	}

	public DrawableAnchoredLines createDrawableString(String name) {
		return new DrawableAnchoredLines(getRawString(name)).setConstant();
	}

	public DrawableAnchoredLines createDrawableFormatString(String name) {
		return (DrawableAnchoredLines)new DrawableAnchoredLines().allocFormatString(getRawString(name));
	}

	public DrawableAnchoredLines createDrawableFormatString(String name,float anchor) {
		return (DrawableAnchoredLines)new DrawableAnchoredLines().setHorizontalAnchor(anchor).allocFormatString(getRawString(name));
	}

	public void clear() {
		mStrings.clear();
	}

	public void createKeyIfMissing(String key) {
		if(!mFallbackStringsXML.mStrings.containsKey(key)) {
			mFallbackStringsXML.mStrings.put(key, UNKNOWN_KEY);
		}
	}

	public void save(PrintStream stream) {
		stream.println(XML_HEADER);
		stream.println(RESOURCES_OPEN);
		
		for(Entry<String,String> e: mFallbackStringsXML.mStrings.entrySet()){
			stream.println("\t<string name=\""+e.getKey()+ "\">"+e.getValue()+"</string>");
		}
		stream.print(RESOURCES_CLOSE);		
	}

}
