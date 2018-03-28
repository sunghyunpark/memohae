package database.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SungHyun on 2018-03-28.
 */

public class MemoVO extends RealmObject{
    @PrimaryKey
    private int no;

}
