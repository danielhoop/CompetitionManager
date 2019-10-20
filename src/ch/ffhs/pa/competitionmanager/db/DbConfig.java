package ch.ffhs.pa.competitionmanager.db;

/**
 * Contains strings for querying the database.
 */
public class DbConfig {

    static String dbConnectionString() {
        return "";
    }
    static String queryAllScoresForEvent(long event, boolean validOnly) {
        return "";
    }
    static String queryAllScoresForCategory(long event, long category, boolean validOnly) {
        return "";
    }
    static String queryAllScoresForCompetitor(long event, long competitor, boolean validOnly) {
        return "";
    }

    /**
     * Give back an SQL string to get the row from 'event' table that has specific id.
     * @return An SQL string to get the row from 'event' table that has specific id.
     */
    public static String eventById(long id) {
        return "select *\n" +
                "from `CompetitionManager`.`event`\n" +
                "where `id` = " + id + ";";
    }

    /**
     * Give back an SQL string that will drop the column called 'age_<eventId>' in table 'competitor'.
     * @param eventId The id of the event.
     * @return An SQL string that will drop the column called 'age_<eventId>' in table 'competitor'.
     */
    public static String dropAgeColumn(long eventId) {
        return "ALTER TABLE `CompetitionManager`.`competitor`\n" +
                "DROP COLUMN `age_" + eventId + "`;";
    }

    /**
     * Give back an SQL string that wil create an age column in table 'competitor' which contains the age in years.
     * @param eventId The id of the event.
     * @param dateOfToday A date string ISO-8601 format uuuu-MM-dd, like '2019-01-01'.
     * @return An SQL string that wil create an age column in table 'competitor' which contains the age in years.
     */
    static String createAgeColumn(long eventId, String dateOfToday) {
        return "ALTER TABLE `CompetitionManager`.`competitor`\n" +
                "ADD COLUMN `age_" + eventId + "` TINYINT unsigned\n" +
                "AS (\n" +
                "    YEAR(" + dateOfToday + ") -\n" +
                "    YEAR(`date_of_birth`) - \n" +
                "    IF(STR_TO_DATE(CONCAT(YEAR(" + dateOfToday + "), '-', MONTH(`date_of_birth`), '-', DAY(`date_of_birth`)) ,'%Y-%c-%e') > " + dateOfToday + ", 1, 0)\n" +
                ");";
    }

    /**
     * Given an event id, returns an SQL string that will count the number of valid scores in table 'score'.
     * @param event The id of the event.
     * @return An SQL string that will count the number of valid scores in table 'score'.
     */
    public static String numberOfValidScores(long event) {
        return "select sum(`is_valid`)\n" +
                "from `CompetitionManager`.`score`\n" +
                "where `event_id` = " + event + ";";
    }

    /**
     * Given an event id, returns an SQL string that will return the maximum id in table 'score'.
     * @param event The id of the event.
     * @return An SQL string that will return the maximum id in table 'score'
     */
    public static String maxIdInScores(long event) {
        return "select max(`id`)\n" +
                "from `CompetitionManager`.`score`\n" +
                "where `is_valid` = true\n" +
                "  and `event_id` = " + event + ";";
    }

    // --------------------------------------------
    // Methods to create SQL strings for categories
    // --------------------------------------------
    public static String getAllCategories(long eventId) {
        return "select *\n" +
                "    from `CompetitionManager`.`category`\n" +
                "    where `event_id` = " + eventId + ";";
    }
    public static String getAllViewNames() {
        return "SHOW FULL TABLES IN `CompetitionManager` WHERE TABLE_TYPE LIKE '%VIEW%';";
    }
    public static String categoryViewEventName(long eventId) {
        return "scores_category_" + eventId;
    }
    public static String categoryViewFullName(long eventId, long categoryId) {
        return "scores_category_" + eventId + "_" + categoryId;
    }
    public static String dropView(String viewName) {
        return "drop view `CompetitionManager`.`" + viewName + "`;";
    }
    public static String createScoreViewForCategory(String viewName, long eventId, int minAgeInclusive, int maxAgeInclusive, int gender) {
        return "create view `CompetitionManager`.`" + viewName + "` as\n" +
                "select s.*, c.`name`, c.`date_of_birth`\n" +
                "    from `CompetitionManager`.`score` s\n" +
                "    left join `CompetitionManager`.`competitor` c\n" +
                "        on s.`competitor_id` = c.`id`\n" +
                "    where     s.`deleted` = false\n" +
                "          AND s.`is_valid` = true\n" +
                "          AND s.`event_id` = "+ eventId + "\n" +
                "          AND c.age >= " + minAgeInclusive + "\n" +
                "          AND c.age <= " + maxAgeInclusive + "\n" +
                "          AND c.gender = " + gender + ";";
    }
    public static String createScoreViewForAllCompetitors(String viewName, long eventId) {
        return "create view `CompetitionManager`.`" + viewName + "` as\n" +
                "select s.*, c.`name`, c.`date_of_birth`\n" +
                "    from `CompetitionManager`.`score` s\n" +
                "    left join `CompetitionManager`.`competitor` c\n" +
                "        on s.`competitor_id` = c.`id`\n" +
                "    where     s.`deleted` = false\n" +
                "          AND s.`is_valid` = true\n" +
                "          AND s.`event_id` = "+ eventId + ";";
    }
}
