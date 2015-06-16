/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.drive.sample.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveApi.DriveIdResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

/**
 * Activity to illustrate how to retrieve and read file contents.
 */
public class RetrieveContentsActivity extends BaseDemoActivity {

    private static final String TAG = "RetrieveContentsActivity";
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image);
        imageView = (ImageView) findViewById(R.id.image_content);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);

        Query query = new Query.Builder()
                .addFilter(Filters.or(
                        Filters.eq(SearchableField.MIME_TYPE, "image/png"),
                        Filters.eq(SearchableField.MIME_TYPE, "image/jpeg")))
                .build();

        Drive.DriveApi.query(getGoogleApiClient(), query)
                .setResultCallback(metadataCallback);

    }

    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback =
            new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving results");
                        return;
                    }

                    MetadataBuffer metaDatas = result.getMetadataBuffer();

                    for (Metadata m : metaDatas) {
                        new RetrieveDriveFileContentsAsyncTask(
                                RetrieveContentsActivity.this).execute(m.getDriveId());
                    }
                }
            };

    final private class RetrieveDriveFileContentsAsyncTask
            extends ApiClientAsyncTask<DriveId, Boolean, Bitmap> {

        public RetrieveDriveFileContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected Bitmap doInBackgroundConnected(DriveId... params) {
            Bitmap bitmap = null;
            DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(), params[0]);
            DriveContentsResult driveContentsResult = file.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return null;
            }
            DriveContents driveContents = driveContentsResult.getDriveContents();
            bitmap = BitmapFactory.decodeStream(driveContents.getInputStream());

            driveContents.discard(getGoogleApiClient());
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result == null) {
                showMessage("Error while reading from the file");
                return;
            }
            imageView.setImageBitmap(result);
        }
    }
}
