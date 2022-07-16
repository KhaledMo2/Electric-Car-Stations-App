package com.example.ladesaeulen;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Server access (file download) as an example of a class of service
 */
public  class RequestService extends Service {
    /**
     * @param intent - Intent used when calling the service
     * @return binder - communication object to the service
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Identifier for bundle parameters: URL
     */
    public static final String REQUESTURL = "NZSE.url";
    /**
     * Identifier for bundle parameters: Id to image
     */
    public static final String UNIQUEID = "NZSE.FileId";
    /**
     * Bezeichner für Bundleparameter: Dateiname
     */
    public static final String FILENAME = "NZSE.filename";
    /**
     * Identifier for bundle parameters: filename
     */
    public static final String FILEPATH = "NZSE.filepath";
    /**
     * Identifiers for bundle parameters:
     * Activity.RESULT_OK or Activity.RESULT_CANCEL
     */
    public static final String RESULT = "NZSE.result";
    /**
     * Identifier for bundle parameters: brief notification
     */
    public static final String NOTIFICATION = "NZSE.notification";

    private Handler uiServiceCallbackHandler;
    //----------------------------

    /**
     * Binder for the client
     */
    private final IBinder mBinder = new RequestServiceBinder();

    /**
     * Class RequestServiceBinder for the client.
     * This service runs in the same process as the client activity;
     * therefore no further communication effort (IPC) is required
     */
    public  class RequestServiceBinder extends Binder {
        /**
         * @return provides an instance so that the client can use the public methods
         */
        RequestService getService() {
            final  RequestService requestService = RequestService.this;
            return requestService;
        }

        /**
         * Access to the download method in the RequestService
         *
         * @param id - identifier
         * @param urlPath - URL
         * @param filePath - local path
         * @param fileName - local filename
         */
        void runURLDownload
        (final String id, final String urlPath, final String filePath,
         final String fileName) {
            getService().runURLDownload(id, urlPath, filePath, fileName);
        }

        /**
         * For returning results
         *
         * @param callbackHandler
         */
        public void setCallback(Handler callbackHandler) {
            getService().setCallback(callbackHandler);
        }

    } // RequestServiceBinder

    /**
     * method is called by system/android,
     * when a service (with startService) is started
     *
     * @param intent - intent of startService call
     * @param flags - flags is 0 or a combination of
     * START_FLAG_REDELIVERY or START_FLAG_RETRY
     * @param startId - unique id
     * @return return is one of START_STICKY_COMPATIBILITY,
     * START_STICKY, START_NOT_STICKY, or START_REDELIVER_INTENT
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    /**
     * Register callback
     *
     * @param callbackHandler - Provide handler in client
     */
    public void setCallback(Handler callbackHandler) {
        uiServiceCallbackHandler = callbackHandler;
    }

    /**
     * constructor
     */
    public RequestService() {
        super();
        Log.i("RequestService", "*** wird erzeugt! ***");
    }

    /**
     * Organize and start download as a thread
     *
     * @param id unique identifier
     * @param urlPath URL
     * @param filePath local directory (cache)
     * @param fileName file name (on the server and locally)
     */
    private void runURLDownload
    (final String id, final String urlPath, final String filePath,
     final String fileName) {
        DownloadThread dThread = new DownloadThread(id, urlPath, filePath, fileName);
        dThread.start();
    }

    /**
     * Organize callback
     *
     * @param uniqueId unique identifier
     * @param outputPath local directory path
     * @param result Activity.RESULT_OK or Activity.RESULT_CANCELED
     */
    private void deliverResults(String uniqueId, String outputPath, int result, int size) {

        Message msg = new Message();

        Bundle bundle = new Bundle();
        bundle.putString(NOTIFICATION, String.valueOf(size)); // oder "Download erfolgreich beendet.");
        bundle.putString(FILEPATH, outputPath);
        bundle.putString(UNIQUEID, uniqueId);
        bundle.putInt(RESULT, result);

        msg.setData(bundle);
        uiServiceCallbackHandler.sendMessage(msg); // Callback
    }

    /**
     * Organization of the download thread
     */
    class DownloadThread extends Thread {
        /**
         * unique identifier
         */
        private String uniqueId;
        /**
         * URL
         */
        private String urlPath;
        /**
         * Directory
         */
        private String filePath;
        /**
         * filename
         */
        private String fileName;
        /**
         * Activity.RESULT_OK, Activity.RESULT_CANCELED
         */
        private int result;

        /**
         * Downloads
         *
         * @param id - Id to the image
         * @param urlPath - URL
         * @param filePath - directory
         * @param fileName - file name
         */
        DownloadThread(final String id, final String urlPath, final String filePath, final String fileName) {
            this.result = AppCompatActivity.RESULT_CANCELED;
            this.uniqueId = id;
            this.urlPath = urlPath;
            this.filePath = filePath;
            this.fileName = fileName;
        }

        /**
         * specific run method
         * Connect to server, download data and
         * save locally in a file (=in your own cache).
         */
        public void run() {
            try {
                String myfolder = Environment.getExternalStorageDirectory().getPath() + "/" + filePath;

                File f;
                f = new File(myfolder);
                System.out.println("(run) directory: " + myfolder);
                // erzeuge Verzeichnis (Folder), falls erforderlich
                if (!f.exists()) {
                    boolean b = f.mkdir();
                    if (!b)
                        Log.i("(run) NZSE: ", "directory " + filePath + " could not be created.");
                }

                String[] dateien = new File(myfolder).list();
                for (String d : dateien)
                    System.out.println("file in directory:" + d);

                FileOutputStream fos = null;

                File output = new File(
                        Environment.getExternalStorageDirectory().getPath() + "/" + this.filePath + "/" + this.fileName);

                if (output.exists()) output.delete();

                HttpURLConnection urlConnection = null;
                InputStream inputStream = null;
                int downloadedSize = 0;
                try {
                    Log.i("(run) FILE READER", "!... some input **********************");
                    Log.i("(run) FILE READER", "!... " + output.getPath());

                    System.out.println("(run) urlPath: " + urlPath);
                    URL url = new URL(urlPath);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    //urlConnection.setRequestProperty("Connection", "keep-alive");
                    //urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0\n");
                    //urlConnection.setRequestProperty("Accept", "text/csv");
                    //urlConnection.setDoOutput(true);
                    urlConnection.connect();

                    System.out.println("(run) urlPath: ... read");
//------------------------------------------------
                    System.out.println("... reading:");
                    inputStream = urlConnection.getInputStream();

                    fos = new FileOutputStream(output.getPath() );

                    byte[] buffer = new byte[2048];
                    int bufferLength;
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                    }

                    System.out.println("(run) Buffer: " + buffer);
                    System.out.println("(run) BufferLength: " + bufferLength);
                    Log.i("(run) Progress: ", "downloadedSize: " + downloadedSize);

                    // successfully finished
                } catch (Exception e) {
                    Log.i("(run) Exception:", " URLConnection");
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            Log.i("(run) Exception:", " InputStream null");
                            e.printStackTrace();
                        }
                    } // stream
                    if (fos != null) {
                        try {
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            Log.i("(run) Exception:", " close fails");
                            e.printStackTrace();
                        }
                    } // fos
                }// finally

                this.result = AppCompatActivity.RESULT_OK;
                deliverResults(this.uniqueId, output.getAbsolutePath(), this.result, downloadedSize);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }// run
    } // DownloadThread
} // RequestService