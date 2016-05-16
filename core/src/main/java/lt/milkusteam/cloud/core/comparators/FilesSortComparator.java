package lt.milkusteam.cloud.core.comparators;

import com.google.api.services.drive.model.File;

import java.util.Comparator;

/**
 * Created by Vilintas Strielčiūnas on 2016-04-20.
 */
public class FilesSortComparator implements Comparator<File> {

    private static final String FOLDER = "folder";
    @Override
    public int compare(File o1, File o2) {
        boolean pf = o1.getMimeType().equals(FOLDER);
        boolean af = o2.getMimeType().equals(FOLDER);
        return (pf && af) ? o1.getName().compareTo(o2.getName()) :
                (pf && !af) ? -1 :
                        (!pf && af) ? 1 : o1.getName().compareTo(o2.getName());
    }
}
