package yang.surface;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import yang.graphics.font.DrawableAnchoredLines;

//TODO xml comments not working
public class StringsXML {

	public static final String UNKNOWN_KEY = "<>";
	private final HashMap<String, String> mStrings;
	private StringsXML mFallbackStringsXML = null;

	StringsXML() {
		mStrings = new HashMap<String ,String>();
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

	public StringsXML load(InputStream xmlStream) {
		mStrings.clear();
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
				mStrings.put(key, string);
			}

			xmlStream.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return this;
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

}
