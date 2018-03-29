package database.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SungHyun on 2018-03-28.
 */

public class MemoVO extends RealmObject{
    @PrimaryKey
    private int no;
    private String memoText;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getMemoText() {
        return memoText;
    }

    public void setMemoText(String memoText) {
        this.memoText = memoText;
    }


}
