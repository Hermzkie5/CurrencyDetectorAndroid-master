package org.tensorflow.lite.examples.classification;

import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class HomePageActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = HomePageActivity.class.getSimpleName();
    public static Intent newIntent(Context context) { Log.d(TAG,"newIntent()");
        return new Intent(context.getApplicationContext(), HomePageActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    private ImageButton captureButton, uploadButton, exploreButton, homeButton;
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) { Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        Log.d(TAG,"onCreate()");
        captureButton = findViewById(R.id.captureButton);
        uploadButton = findViewById(R.id.uploadButton);
        exploreButton = findViewById(R.id.exploreButton);
        homeButton = findViewById(R.id.homeButton);

        //launchCamera();
    }

    private void launchCamera() { Log.d(TAG,"launchCamera()");
        startActivity(new Intent(this, ClassifierActivity.class));
    }

    private void launchUpload() { Log.d(TAG,"launchUpload()");
        Toast.makeText(this,"Gallery",Toast.LENGTH_SHORT).show();
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI) , GALLERY_REQUEST);
    }

    private void launchExplore() { Log.d(TAG,"launchExplore()");
        Toast.makeText(this,"exploreButton",Toast.LENGTH_SHORT).show();
    }

    private void launchHome() { Log.d(TAG,"launchHome()");
        Toast.makeText(this,"homeButton",Toast.LENGTH_SHORT).show();
    }

    private void launchScanning(String path) { Log.d(TAG,"launchScanning()");
        startActivity(ScanningActivity.newIntent(this, path));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.captureButton:
                Toast.makeText(this,"captureButton",Toast.LENGTH_SHORT).show();
                launchCamera();
                    break;
            case R.id.uploadButton:
                launchUpload();
                break;
            case R.id.exploreButton:
                launchExplore();
                break;
            case R.id.homeButton:
                launchHome();
                break;
            default:
                    break;
        }
    }

    @Override
    protected void onResume() { Log.d(TAG,"onResume()");
        super.onResume();
        captureButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        exploreButton.setOnClickListener(this);
        homeButton.setOnClickListener(this);
    }

    @Override
    protected void onPause() { Log.d(TAG,"onPause()");
        super.onPause();
        captureButton.setOnClickListener(null);
        uploadButton.setOnClickListener(null);
        exploreButton.setOnClickListener(null);
        homeButton.setOnClickListener(null);
    }
    //region Gallery Methods
    private Boolean isPrimary(String type) {
        return "primary".equalsIgnoreCase(type);
    }

    private Boolean isImage(String type) {
        return "image".equals(type);
    }

    private Boolean isVideo(String type) {
        return "video".equals(type);
    }

    private Boolean isAudio(String type) {
        return "audio".equals(type);
    }

    private boolean isDocumentUri(Application application, Uri uri) {
        return DocumentsContract.isDocumentUri(application, uri);
    }

    private boolean isContent(Uri uri) {
        return "content".equalsIgnoreCase(uri.getScheme());
    }

    private boolean isFile(Uri uri) {
        return "file".equalsIgnoreCase(uri.getScheme());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private Boolean isDownloadsDocument(Uri uri) { Log.d(TAG,"isDownloadsDocument($uri)");
        return "com.android.providers.downloads.documents" == uri.getAuthority();
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private Boolean isExternalStorageDocument(Uri uri) { Log.d(TAG,"isExternalStorageDocument($uri)");
        return "com.android.externalstorage.documents" == uri.getAuthority();
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private Boolean isMediaDocument(Uri uri) { Log.d(TAG,"isMediaDocument($uri)");
        return "com.android.providers.media.documents" == uri.getAuthority();
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] proj = { column };
        String value;
        try { Log.d(TAG, "getDataColumn() try");
            cursor = getApplication().getContentResolver().query(uri, proj, selection, selectionArgs, null);
            int column_index = cursor.getColumnIndexOrThrow(column);
                    cursor.moveToFirst();
            Log.d(TAG,"getDataColumn($uri,$selection,$selectionArgs) : ${cursor.getString(column_index)}");
            value = cursor.getString(column_index);
        } catch (IllegalArgumentException ex) { ex.printStackTrace();
            Log.e(TAG, "getDataColumn IllegalArgumentException : " + ex.getMessage() + " " + ex.getCause());
            value = Constants.Empty;
        } catch (Exception ex) { ex.printStackTrace();
            Log.e(TAG, "getDataColumn Exception : " + ex.getMessage() + " " + ex.getCause());
            value = Constants.Empty;
        } finally { Log.d(TAG, "getDataColumn() finally");
            cursor.close();
        }
        return value;
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private Boolean isGooglePhotosUri(Uri uri) {
        Log.d(TAG,"isGooglePhotosUri($uri)");
        return "com.google.android.apps.photos.content" == uri.getAuthority();
    }

    private String getMediaStorePathFromURI(Uri uri) { Log.d(TAG,"getMediaStorePathFromURI($uri)");
        if (isGooglePhotosUri(uri)) { Log.d(TAG,"true ${uri.getLastPathSegment()}");
            return uri.getLastPathSegment();
        } else { Log.d(TAG,"false ${getDataColumn(uri, null, null)}");
            return getDataColumn(uri, null, null);
        }
    }

    private String getKitkatPathFromURI(Uri uri) {
        if (isDocumentUri(getApplication(), uri) && isExternalStorageDocument(uri) || isMediaDocument(uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            String[] split = docId.split(":");
            String type = split[0];
            String selection = "_id=?";
            String[] selectionArgs = { split[1] };
            if(isPrimary(type)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1];
            } else if(isImage(type)) {Log.d(TAG,"isImage($type)");
                return getDataColumn(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection, selectionArgs
                );
            } else if(isVideo(type)) { Log.d(TAG,"isVideo($type)");
                return getDataColumn(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        selection, selectionArgs
                );
            } else if (isAudio(type)) { Log.d(TAG,"isAudio($type)");
                return getDataColumn(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        selection, selectionArgs
                );
            } else { Log.d(TAG,"else");
                return getDataColumn(uri,null,null);
            }
        } else if(isDocumentUri(getApplication(), uri) && isDownloadsDocument(uri)) {
            Log.d(TAG,"isDocumentUri(getApplication(), uri) && isDownloadsDocument(uri)");
            String id = DocumentsContract.getDocumentId(uri);
            if (!TextUtils.isEmpty(id)) { Log.d(TAG,"!TextUtils.isEmpty(id)");
                try { Log.d(TAG, "isDocumentUri(getApplication(), $uri) && isDownloadsDocument($uri) try");
                    return getDataColumn(
                            ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"),
                                    java.lang.Long.valueOf(id)
                            ),
                            null,
                            null
                    );
                } catch (NumberFormatException ex) { ex.printStackTrace();
                    Log.e(TAG, "getKitKatPathFromURI NumberFormatException : ${ex.message}");
                    return null;
                }
            } else { Log.d(TAG,"else");
                return getDataColumn(uri,null,null);
            }
        } else if(isContent(uri)) { //MediaStore (and general)
            Log.d(TAG,"isContent($uri) ${getMediaStorePathFromURI(uri)}");
            return getMediaStorePathFromURI(uri);
        } else if(isFile(uri)) {/* File */ Log.d(TAG,"isFile($uri) ${uri.getPath()}");
            return uri.getPath();
        } else { Log.d(TAG,"else");
            return getDataColumn(uri,null,null);
        }
    }

    private String getPathFromURI(Uri uri) { Log.d(TAG,"getPathFromURI(" + uri + ")");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //DocumentProvider
            Log.d(TAG,"Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT");
            return getKitkatPathFromURI(uri);
        } else if(isContent(uri)) { /* MediaStore (and general) */ Log.d(TAG,"isContent($uri)");
            return getMediaStorePathFromURI(uri);
        } else if(isFile(uri)) { /* File */ Log.d(TAG,"isFile($uri)");
            return uri.getPath();
        } else { Log.d(TAG,"else");
            return getDataColumn(uri,null,null);
        }
    }
    //endregion
    private void decodeImage(Uri selectedImage) { Log.d(TAG,"decodeImage( " + selectedImage + " ) -> " + getPathFromURI(selectedImage));
        /*if (getPathFromURI(selectedImage).length() > 0) {
            launchScanning(getPathFromURI(selectedImage));
        }*/
        launchScanning(selectedImage.toString());
        /*String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Bitmap decodedFile = null;
        Cursor cursor = null;
        try { Log.d(TAG,"decodeImage try");
            if (selectedImage != null) {
                cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    decodedFile =  BitmapFactory.decodeFile(picturePath);
                    //imageView.setImageBitmap(decodedFile);
                    Log.d(TAG,"File Path " + decodedFile);
                }
            }
        } catch (Exception exception) { Log.d(TAG,"decodeImage catch " + exception.getMessage());

        } finally { Log.d(TAG,"decodeImage finally ");
            if (cursor != null) {
                cursor.close();
            }
            if (decodedFile != null) {
                launchScanning(decodedFile);
            }
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"onActivityResult(" + requestCode + ", resultCode" + resultCode + ", " + data + ")");
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            decodeImage(data.getData());
        } else {

        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy()");
        super.onDestroy();
    }
}