package com.loganlinn.pivotaltracker.util;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ParserUtils {
	
	
	private static XmlPullParserFactory factory_;
	
	public static XmlPullParser newPullParser(InputStream input) throws XmlPullParserException {
		if(factory_ == null){
			factory_ = XmlPullParserFactory.newInstance();
		}
		final XmlPullParser parser = factory_.newPullParser();
		parser.setInput(input, null);
		return parser;
	}
}
