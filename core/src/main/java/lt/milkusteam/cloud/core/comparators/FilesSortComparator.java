package lt.milkusteam.cloud.core.comparators;

import com.google.api.services.drive.model.File;

import java.util.Comparator;

/**
 * Created by Vilintas Strielčiūnas on 2016-04-20.
 */
public class FilesSortComparator implements Comparator<File> {
    @Override
    public int compare(File o1, File o2) {
        boolean pf = o1.getMimeType().equals("folder");
        boolean af = o2.getMimeType().equals("folder");
        return (pf == true && af == true) ? o1.getName().compareTo(o2.getName()) :
                (pf == true && af == false) ? -1 :
                        (pf == false && af == true) ? 1 : o1.getName().compareTo(o2.getName());
    }
}
