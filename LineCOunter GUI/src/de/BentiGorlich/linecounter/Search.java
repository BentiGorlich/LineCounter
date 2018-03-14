package de.BentiGorlich.linecounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;

import javafx.concurrent.Task;

public class Search extends Task<Count>{
	
	File tosearch;
	long lines = 0;
	long chars = 0;
	long docs = 0;
	long bytes = 0;
	long max_chars = 0; 
	long max_lines = 0;
	long max_bytes = 0;
	String max_lines_doc;
	String max_chars_doc;
	String max_bytes_doc;
	String[] types;
	ArrayList<File> files = new ArrayList<File>();
	
	long curr_docs = 0;
	
	Search(File path_f, String[] types){
		tosearch = path_f;
		this.types = types;
	}
	
	@Override
	protected Count call() throws Exception {
		if(!tosearch.exists()) {
			return null;
		}
		init(tosearch);
		for(int i = 0; i<files.size(); i++) {
			count(files.get(i));
		}
		Count c = new Count(lines, chars, docs, max_lines, max_chars, bytes, max_bytes, max_lines_doc, max_chars_doc, max_bytes_doc);
		updateValue(c);
		return c;
	}
	
	private void init(File f) {
		if(f.isDirectory()) {
			File[] files = f.listFiles();
			for(int i = 0; i<files.length; i++) {
				init(files[i]);
			}
		}else{
			boolean count = false;
			String name = f.getName();
			String ext = name.substring(name.lastIndexOf(".") + 1);
			if(types.length == 0) {
				count = true;
			}
			for(int i = 0; i<types.length; i++) {
				if(ext.equals(types[i])) {
					count = true;
				}
			}
			if(count) {
				files.add(f);
				docs++;
			}
		}
	}

	private void count(File f) throws IOException {
		if(f.canRead()) {
			try {
				if(!isFileLock(f)){
					System.out.println(f.getAbsolutePath());
					bytes = bytes + f.length();
					long thisbytes = f.length();
					if(thisbytes > max_bytes) {
						max_bytes = thisbytes;
						max_bytes_doc = f.getAbsolutePath();
					}
					BufferedReader bf = new BufferedReader(new FileReader(f));
					String line = "";
					long lines_count = 0, chars_count = 0; 
					while((line = bf.readLine()) != null) {
						lines++;
						lines_count++;
						chars = chars + line.length();
						chars_count = chars_count + line.length();
					}
					if(max_chars < chars_count) {
						max_chars = chars_count;
						max_chars_doc = f.getAbsolutePath();
					}
					if(max_lines < lines_count) {
						max_lines = lines_count;
						max_lines_doc = f.getAbsolutePath();
					}
					bf.close();
					curr_docs++;
					updateProgress(curr_docs, docs);
				}
			}catch(FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	public boolean isFileLock(File file) throws IOException {
	    boolean isLock = true;
        RandomAccessFile rf = new RandomAccessFile(file, "rw");
        FileChannel fileChannel = rf.getChannel();
        FileLock lock = null;
        try {
            // let us try to get a lock. If file already has an exclusive lock by another process
            lock = fileChannel.tryLock();
            if (lock != null) {
                isLock = false;
            }
        } catch (Exception ex) {
        	System.out.println("Error when checkFileLock: " + ex);
        } finally {
            if (lock != null) {
                lock.release();
            }
            if(fileChannel != null){
                fileChannel.close();
            }
            if(rf != null){
                rf.close();
            }
        }
	    return isLock;
	}
}
