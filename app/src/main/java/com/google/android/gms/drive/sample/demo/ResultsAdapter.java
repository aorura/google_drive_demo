/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.drive.sample.demo;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.widget.DataBufferAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A DataBufferAdapter to display the results of file listing/querying requests.
 */
public class ResultsAdapter extends DataBufferAdapter<Metadata> {
    final static String LOG_TAG = "ResultAdapter";
    Context context;
    PrintWriter output;

    public ResultsAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        this.context = context;

        File file = getFile("park.txt");
        output = getPrintWriter(file);
    }

    protected void finalize() {
        if (output != null) {
            output.close();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(),
                    android.R.layout.simple_list_item_1, null);
        }
        Metadata metadata = getItem(position);
        TextView titleTextView = (TextView) convertView.findViewById(android.R.id.text1);
        StringBuilder sb = new StringBuilder();
        sb.append(metadata.getTitle());
        sb.append(" ");
        sb.append(metadata.getDriveId().getResourceId());

        titleTextView.setText(sb.toString());

        if (output != null) {
            output.println(sb.toString());
            output.flush();
        }

        return convertView;
    }

    private File getFile(String fileName) {
        File dir = null;
        if (isExternalStorageWritable()) {
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), LOG_TAG);
            if (!dir.mkdirs()) {
                Log.e(LOG_TAG, "Directory not created");
            }
        }

        File newFile = new File(dir, fileName);
        try {
            newFile.createNewFile();
        } catch (IOException e) {
            Log.e(LOG_TAG, "newFile IOException");
            return null;
        }
        return newFile;
    }

    public PrintWriter getPrintWriter(File file) {
        PrintWriter pw = null;
        FileOutputStream f = null;
        if (file != null) {
            try {
                f = new FileOutputStream(file);
                pw = new PrintWriter(f);
            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, "FileNotFoundException");
            } catch (IOException e) {
                Log.e(LOG_TAG, "IOException");
            }
        }

        return pw;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}