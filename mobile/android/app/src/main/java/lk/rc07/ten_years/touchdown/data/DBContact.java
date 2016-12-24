package lk.rc07.ten_years.touchdown.data;

/**
 * Created by Sabri on 12/24/2016. database definition
 */

class DBContact {

    static abstract class MatchTable {
        static final String TABLE_NAME = "match_table";
        static final String COLUMN_ID = "match_id";
        static final String COLUMN_TEAM_TWO = "match_team_one";
        static final String COLUMN_TEAM_ONE = "match_team_two";
        static final String COLUMN_VENUE = "match_venue";
        static final String COLUMN_DATE = "match_date";
        static final String COLUMN_STATUS = "match_status";
        static final String COLUMN_RESULT = "match_result";
        static final String COLUMN_LEAGUE = "match_league";
        static final String COLUMN_ROUND = "match_round";
    }


    static abstract class PlayerTable {
        static final String TABLE_NAME = "player_table";
        static final String COLUMN_ID = "player_id";
        static final String COLUMN_NAME = "player_name";
        static final String COLUMN_IMG = "player_img";
        static final String COLUMN_TEAM = "player_team";
        static final String COLUMN_WEIGHT = "player_weight";
        static final String COLUMN_HEIGHT = "player_height";
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
}
