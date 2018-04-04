package database.model;

import android.net.Uri;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SungHyun on 2018-03-28.
 */

public class MemoVO extends RealmObject{
    @PrimaryKey
    private int no;
    private int order;
    private boolean isSecreteMode;
    private String memoText;
    private String memoPhotoPath;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isSecreteMode() {
        return isSecreteMode;
    }

    public void setSecreteMode(boolean secreteMode) {
        isSecreteMode = secreteMode;
    }

    public String getMemoText() {
        return memoText;
    }

    public void setMemoText(String memoText) {
        this.memoText = memoText;
    }

    public String getMemoPhotoPath() {
        return memoPhotoPath;
    }

    public void setMemoPhotoPath(String memoPhotoPath) {
        this.memoPhotoPath = memoPhotoPath;
    }


}
