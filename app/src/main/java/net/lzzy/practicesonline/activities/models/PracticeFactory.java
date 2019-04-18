package net.lzzy.practicesonline.activities.models;
;
import net.lzzy.practicesonline.activities.constants.DbConstants;
import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.sqllib.DbPackager;
import net.lzzy.sqllib.SqlRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzzy_gxy on 2019/4/17.
 * Description:
 */
public class PracticeFactory {
    private static final PracticeFactory OUR_INSTANCE = new PracticeFactory();
    private SqlRepository<Practice> repository;

    public static PracticeFactory getInstance() {
        return OUR_INSTANCE;
    }

    private PracticeFactory() {
        repository = new SqlRepository<>(AppUtils.getContext(), Practice.class, DbConstants.packager);
    }

    public List<Practice> get() {
        return repository.get();
    }

    public Practice getById(String id) {
        return repository.getById(id);
    }

    public List<Practice> search(String kw) {
        try {
            return repository.getByKeyword(kw, new String[]{Practice.COL_NAME, Practice.COL_OUTLINES}, false);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean add(Practice practice) {
        if (isPracticeInDb(practice)) {
            return false;
        }
        repository.insert(practice);
        return true;
    }

    public boolean isPracticeInDb(Practice practice) {
        try {
            return repository.getByKeyword(String.valueOf(practice.getApild()),
                    new String[]{Practice.COL_APILD}, true)
                    .size() > 0;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return true;
        }
    }
}
