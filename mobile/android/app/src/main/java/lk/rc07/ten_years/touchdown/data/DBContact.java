package lk.rc07.ten_years.touchdown.data;

/**
 * Created by Sabri on 12/24/2016. database definition
 */

public class DBContact {

    static abstract class MatchTable {
        static final String TABLE_NAME = "match_table";
        static final String COLUMN_ID = "match_id";
        static final String COLUMN_TEAM_TWO = "match_team_one";
        static final String COLUMN_TEAM_ONE = "match_team_two";
        static final String COLUMN_VENUE = "match_venue";
        static final String COLUMN_DATE = "match_date";
        static final String COLUMN_STATUS = "match_status";
        static final String COLUMN_RESULT = "match_result";
        static final String COLUMN_LAST = "match_last";
        static final String COLUMN_LONGITUDE = "match_longitude";
        static final String COLUMN_LATITUDE = "match_latitude";
        static final String COLUMN_GROUP = "match_group";
        static final String COLUMN_ALBUM = "match_album";
    }


    static abstract class PlayerTable {
        static final String TABLE_NAME = "player_table";
        static final String COLUMN_ID = "player_id";
        static final String COLUMN_NAME = "player_name";
        static final String COLUMN_IMG = "player_img";
        static final String COLUMN_TEAM = "player_team";
        static final String COLUMN_WEIGHT = "player_weight";
        static final String COLUMN_HEIGHT = "player_height";
        static final String COLUMN_COLORS = "player_colors";
        static final String COLUMN_BIRTH = "player_birth";
    }

    static abstract class PlayerPositionTable {
        static final String TABLE_NAME = "player_position_table";
        static final String COLUMN_PLAYER_ID = "player_pos_player_id";
        static final String COLUMN_POS_ID = "player_pos_pos_id";
        static final String COLUMN_MATCH_ID = "player_pos_match_id";
    }

    static abstract class PointsTable {
        static final String TABLE_NAME = "points_table";
        static final String COLUMN_ID = "points_id";
        static final String COLUMN_TEAM = "points_team";
        static final String COLUMN_PLAYED = "points_played";
        static final String COLUMN_WON = "points_won";
        static final String COLUMN_LOST = "points_lost";
        static final String COLUMN_POINTS = "points_points";
        static final String COLUMN_GROUP = "points_group";
    }

    static abstract class PositionTable {
        static final String TABLE_NAME = "position_table";
        static final String COLUMN_ID = "position_id";
        static final String COLUMN_NO = "position_no";
        static final String COLUMN_NAME = "position_name";
    }

    static abstract class ScoreTable {
        static final String TABLE_NAME = "score_table";
        static final String COLUMN_ID = "score_id";
        static final String COLUMN_MATCH = "score_match";
        static final String COLUMN_TEAM = "score_team";
        static final String COLUMN_SCORE = "score_score";
        static final String COLUMN_DETAILS = "score_details";
        static final String COLUMN_PLAYER = "score_player";
        static final String COLUMN_TIME = "score_time";
        static final String COLUMN_ACTION = "score_action";
    }

    static abstract class TeamTable {
        static final String TABLE_NAME = "team_table";
        static final String COLUMN_ID = "team_id";
        static final String COLUMN_NAME = "team_name";
        static final String COLUMN_FLAG = "team_flag";
        static final String COLUMN_LOGO = "team_logo";
        static final String COLUMN_YEAR = "team_year";
    }

    public static abstract class GroupTable {
        static final String TABLE_NAME = "group_table";
        static final String COLUMN_ID = "group_id";
        public static final String COLUMN_NAME = "group_name";
        public static final String COLUMN_LEAGUE = "group_league";
        public static final String COLUMN_ROUND = "group_round";
        public static final String COLUMN_YEAR = "group_year";
    }

    static abstract class ImageTable {
        static final String TABLE_NAME = "image_table";
        static final String COLUMN_ID = "image_id";
        static final String COLUMN_MATCH = "image_match";
        static final String COLUMN_WIDTH = "image_width";
        static final String COLUMN_HEIGHT = "image_height";
        static final String COLUMN_CREATED = "image_created";
    }

    public static abstract class PlayerTeamTable {
        public static final String TABLE_NAME = "player_team";
        public static final String COLUMN_TEAM_ID = "team_id";
        public static final String COLUMN_PLAYER_ID = "player_id";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_COLORS = "colors";
    }

    public static abstract class PlayerStaffTable {
        public static final String TABLE_NAME = "player_staff";
        public static final String COLUMN_STAFF_ID = "staff_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_POSITION = "position";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_ORDER = "staff_order";
    }
}
