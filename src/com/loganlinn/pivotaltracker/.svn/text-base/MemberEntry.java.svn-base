package com.loganlinn.pivotaltracker;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

import java.io.IOException;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.loganlinn.pivotaltrackie.provider.ProjectContract.Members;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.SyncColumns;

public class MemberEntry extends HashMap<String, String>{
	public static final String[] FIELDS = {
		Members.NAME,
		Members.EMAIL,
		Members.INITIALS,
	};
	
	public interface MemberTags{
		String MEMBER_ID = "id";
	}
	
	public MemberEntry(){
		for(String f : FIELDS){
			put(f,null);
		}
	}

	public void sanitizeNullValues(){
		for(String k: keySet()){
			if(get(k) == null){
				put(k,"");
			}
		}
	}
	
	public static MemberEntry fromParser(XmlPullParser parser)
		throws XmlPullParserException, IOException {
		
		final int depth = parser.getDepth();
		final MemberEntry entry = new MemberEntry();
		String tag = null;
		int type;
		while (((type = parser.next()) != END_TAG || parser.getDepth() > depth)
				&& type != END_DOCUMENT) {
			if (type == START_TAG) {
				tag = parser.getName();
			} else if (type == END_TAG) {
				tag = null;
			} else if (type == TEXT) {
				if(tag != null){
					String text = parser.getText();
					if(MemberTags.MEMBER_ID.equals(tag)){
						entry.put(Members.MEMBER_ID, text);//store as story_id not id
					}else if(entry.containsKey(tag)){
						entry.put(tag, text);
					}
				}
			}
		}
		
		entry.put(SyncColumns.UPDATED, String.valueOf(System.currentTimeMillis()));
		
		return entry;
	}
}
