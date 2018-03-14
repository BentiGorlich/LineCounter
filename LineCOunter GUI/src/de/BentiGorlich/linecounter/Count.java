package de.BentiGorlich.linecounter;

import java.text.DecimalFormat;

public class Count {
	Long total_lines;
	Long total_chars;
	Long total_docs;
	Long max_chars; 
	Long max_lines;
	Long total_bytes;
	Long max_bytes;
	String max_bytes_doc;
	String max_lines_doc;
	String max_chars_doc;
	
	Count(long lines, long chars, long docs, long maxlines, long maxchars, long bytes, long maxbytes, String doc_maxlines, String doc_maxchars, String doc_maxbytes){
		total_chars = chars;
		total_docs = docs;
		total_lines = lines;
		max_chars = maxchars;
		max_lines = maxlines;
		max_lines_doc = doc_maxlines;
		max_chars_doc = doc_maxchars;
		total_bytes = bytes;
		max_bytes = maxbytes;
		max_bytes_doc = doc_maxbytes;
	}

	private String format(Long l) {
		Double last = l.doubleValue();
		Double curr = l.doubleValue();
		int i;
		for(i = 0; curr>1; i++) {
			last = curr;
			curr = curr/1024;
		}
		String suffix = "";
		switch (i - 1){
			case 1: suffix = " KByte"; break;
			case 2: suffix = " MByte"; break;
			case 3: suffix = " GByte"; break;
			case 4: suffix = " TByte"; break;
			default: suffix = " byte"; break;
		}
		DecimalFormat f = new DecimalFormat("#.##");
		return f.format(last) + suffix;
	}
	
	public String getMaxBytes() {
		return format(max_bytes);
	}
	
	public String getBytes() {
		return format(total_bytes);
	}
	
}
