package database;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by SungHyun on 2018-03-28.
 */

public class RealmConfig {
    public RealmConfiguration MemoRealmVersion(Context context){

        Realm.init(context);    //realm 초기화
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("User.realm")
                .schemaVersion(0)
                //.deleteRealmIfMigrationNeeded()
                .migration(new Migration())
                .build();

        return config;
    }
}
