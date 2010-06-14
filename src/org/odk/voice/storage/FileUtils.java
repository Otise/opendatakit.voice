/*
 * Copyright (C) 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.voice.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Static methods used for common file operations.
 * 
 * This class is no longer used heavily as most persistence occurs through the database.
 * 
 * @author Adam Lerer (adam.lerer@gmail.com)
 * @author Carl Hartung (carlhartung@gmail.com)
 * 
 */
public class FileUtils {
  /**
   * Writes a file to the given path, appending digits to the end of the file name until it is unique.
   * @param os The data input stream to write.
   * @param path The path to write to.
   * @return The path of the file written to (which may be different than path, if a file at path already exists).
   */
  public static String writeFile(byte[] data, String path, boolean overwrite) throws IOException {
    OutputStream os = null;
    try {
      if (data == null) throw new IOException("Data is null");
      String path2 = path;
      File f = new File(path2);
      int index = 0;
      while (f.exists() && !overwrite) {
        index++;
        int ext = path.lastIndexOf(".");
        if (ext == -1)
          ext = path.length();
        path2 = path.substring(0, ext) + Integer.toString(index) + path.substring(ext, path.length());
        f = new File(path2);
      }
      if (path.lastIndexOf(File.separator) > 0)
        createFolder(path.substring(0, path.lastIndexOf(File.separator)));
      f.createNewFile();
      System.out.println("Path: " + f.getAbsoluteFile());
      os = new FileOutputStream(f, false);
//      int i;
//      while ((i = is.read()) != -1)
//        os.write(i);
      os.write(data);
      return path2;
    } finally {
      if (os != null)
        try {os.close();}catch(IOException e){}
    }
  }
    
    public static ArrayList<String> getFoldersAsArrayList(String path) {
        ArrayList<String> mFolderList = new ArrayList<String>();
        File root = new File(path);

        if (!storageReady()) {
            return null;
        }
        if (!root.exists()) {
            if (!createFolder(path)) {
                return null;
            }
        }
        if (root.isDirectory()) {
            File[] children = root.listFiles();
            for (File child : children) {
                boolean directory = child.isDirectory();
                if (directory) {
                    mFolderList.add(child.getAbsolutePath());
                }
            }
        }
        return mFolderList;
    }

    public static ArrayList<String> getFilesAsArrayList(String path) {
        ArrayList<String> mFileList = new ArrayList<String>();
        File root = new File(path);

        if (!storageReady()) {
            return null;
        }
        if (!root.exists()) {
            if (!createFolder(path)) {
                return null;
            }
        }
        if (root.isDirectory()) {
            File[] children = root.listFiles();
            for (File child : children) {
                String filename = child.getName();
                // no hidden files
                if (!filename.startsWith(".")) {
                    mFileList.add(child.getAbsolutePath());
                }
            }
        } else {
            String filename = root.getName();
            // no hidden files
            if (!filename.startsWith(".")) {
                mFileList.add(root.getAbsolutePath());
            }
        }
        return mFileList;
    }


    public static ArrayList<String> getFilesAsArrayListRecursive(String path) {
        ArrayList<String> mFileList = new ArrayList<String>();
        File root = new File(path);
        getFilesAsArrayListRecursiveHelper(root, mFileList);
        return mFileList;
    }


    private static void getFilesAsArrayListRecursiveHelper(File f, ArrayList<String> filelist) {
        if (f.isDirectory()) {
            File[] childs = f.listFiles();
            for (File child : childs) {
                getFilesAsArrayListRecursiveHelper(child, filelist);
            }
            return;
        }
        filelist.add(f.getAbsolutePath());
    }


    public static boolean deleteFolder(String path) {
        // not recursive
        if (path != null && storageReady()) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (File file : files) {
                    if (!file.delete()) {
                        //Log.i(t, "Failed to delete " + file);
                    }
                }
            }
            return dir.delete();
        } else {
            return false;
        }
    }


    public static boolean createFolder(String path) {
        if (storageReady()) {
            boolean made = true;
            File dir = new File(path);
            if (!dir.exists()) {
                made = dir.mkdirs();
            }
            return made;
        } else {
            return false;
        }
    }


    public static boolean deleteFile(String path) {
        if (storageReady()) {
            File f = new File(path);
            return f.delete();
        } else {
            return false;
        }
    }

    public static byte[] getFileAsBytes(File file) {

        byte[] bytes = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);

            // Get the size of the file
            long length = file.length();
            if (length > Integer.MAX_VALUE) {
                //Log.e(t, "File " + file.getName() + "is too large");
                return null;
            }

            // Create the byte array to hold the data
            bytes = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int read = 0;
            try {
                while (offset < bytes.length && read >= 0) {
                    read = is.read(bytes, offset, bytes.length - offset);
                    offset += read;
                }
            } catch (IOException e) {
                //Log.e(t, "Cannot read " + file.getName());
                e.printStackTrace();
                return null;
            }

            // Ensure all the bytes have been read in
            if (offset < bytes.length) {
                try {
                    throw new IOException("Could not completely read file " + file.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            return bytes;

        } catch (FileNotFoundException e) {
            //Log.e(t, "Cannot find " + file.getName());
            e.printStackTrace();
            return null;

        } finally {
            // Close the input stream
            try {
                is.close();
            } catch (IOException e) {
                //Log.e(t, "Cannot close input stream for " + file.getName());
                e.printStackTrace();
                return null;
            }
        }



    }


    private static boolean storageReady() {
    	return true;
//        String cardstatus = Environment.getExternalStorageState();
//        if (cardstatus.equals(Environment.MEDIA_REMOVED)
//                || cardstatus.equals(Environment.MEDIA_UNMOUNTABLE)
//                || cardstatus.equals(Environment.MEDIA_UNMOUNTED)
//                || cardstatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
//            return false;
//        } else {
//            return true;
//        }
    }


    public static String getMd5Hash(File file) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(getFileAsBytes(file));
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);
            while (md5.length() < 32)
                md5 = "0" + md5;
            return md5;

        } catch (NoSuchAlgorithmException e) {
            //Log.e("MD5", e.getMessage());
            return null;

        }
    }

    public static boolean fileExists(String path) {
      File f = new File(path);
      return f.exists();
    }



}
