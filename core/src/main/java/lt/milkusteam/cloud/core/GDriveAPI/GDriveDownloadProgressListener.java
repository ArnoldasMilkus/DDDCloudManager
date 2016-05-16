package lt.milkusteam.cloud.core.GDriveAPI;

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;

import java.text.NumberFormat;

/**
 * Created by Vilintas Strielčiūnas on 2016-04-20.
 */
public class GDriveDownloadProgressListener implements MediaHttpDownloaderProgressListener {

        @Override
        public void progressChanged(MediaHttpDownloader downloader) {
            switch (downloader.getDownloadState()) {
                case MEDIA_IN_PROGRESS:
                    ProgressViewer.header2("Download is in progress: " + NumberFormat.getPercentInstance().format(downloader.getProgress()));
                    break;
                case MEDIA_COMPLETE:
                    ProgressViewer.header2("Download is Complete!");
                    break;
            }
        }
}
