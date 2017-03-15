package net.imotto.imottoapp.services.models;

/**
 * Created by sunht on 16/10/31.
 */

public class UserModel {
    public String Id;
    public String UserName;
    public String DisplayName;
    public String Thumb;
    public int Rank;
    public int Change;
    public int Sex;
    public int RelationState;

    public UserStatistic Statistics;

    public class UserStatistic{
        public int Mottos;
        public int Collections;
        public int LovedCollections;
        public int LovedMottos;
        public int Revenue;
        public int Balance;
        public int Bans;
        public int Follows;
        public int Followers;
    }
}
