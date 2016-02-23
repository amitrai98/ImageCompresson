package tablayoutdemo.android.com.imagecompresson;

/**
 * Created by root on 24/2/16.
 */
public class FileUploadModal {
    String base64image = null;
    long fileSize = 0;

    public FileUploadModal(String base64image, long fileSize){
        this.fileSize = fileSize;
        this.base64image = base64image;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getBase64image() {
        return base64image;
    }

    public void setBase64image(String base64image) {
        this.base64image = base64image;
    }


}
