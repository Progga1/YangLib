package yang.util;

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
	private HashMap<String, String> mStrings;
	
	public StringsXML() {
		mStrings = new HashMap<String ,String>();
	}
	
	public StringsXML(InputStream xmlStream) {
		this();
		load(xmlStream);
	}
	
	public StringsXML load(InputStream xmlStream) {
		mStrings.clear();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlStream);
			
			Element root = doc.getDocumentElement();
			NodeList strings = root.getElementsByTagName("string");
			int amount = strings.getLength();
			
			for (int i = 0; i < amount; ++i) {
				Node node = strings.item(i);
				String string = node.getTextContent().trim().replace("\n", "\0").replace("\t", "\0").replace("\\n", "\n").replace("\\t", "\t").replace("\\'", "'");
				String key = node.getAttributes().getNamedItem("name").getTextContent();			
				mStrings.put(key, string);
			}
			
			xmlStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public String getRawString(String name) {
		String toReturn = mStrings.get(name);
		if (toReturn == null) {
			System.err.println("string:" +name +" not found");
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
