package com.google.android.gms.drive.sample.demo;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

/**
 * Created by chalse.park on 2015-06-16.
 */
public class GetDriveId extends BaseDemoActivity {
    private ListView mResultsListView;
    private ResultsAdapter mResultsAdapter;
    private Context context;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        setContentView(R.layout.activity_listfiles);
        mResultsListView = (ListView) findViewById(R.id.listViewResults);
        mResultsAdapter = new ResultsAdapter(this);
        mResultsListView.setAdapter(mResultsAdapter);
        context = this.getApplicationContext();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mResultsAdapter.clear();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        Query query = new Query.Builder()
                .addFilter(Filters.or(
                        Filters.eq(SearchableField.MIME_TYPE, "image/png"),
                        Filters.eq(SearchableField.MIME_TYPE, "text/jpeg")))
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
                    mResultsAdapter.clear();
                    MetadataBuffer metaDatas = result.getMetadataBuffer();

                    for (Metadata m : metaDatas) {
                        Toast.makeText(context, "DriveId: " + m.getDriveId().toInvariantString(), Toast.LENGTH_SHORT).show();
                    }
                    mResultsAdapter.append(result.getMetadataBuffer());
                }
            };
}
