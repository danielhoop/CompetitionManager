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
    public static String eventyById(long id) {
        return "select *\n" +
                "from `CompetitionManager`.`event`\n" +
                "where `id` = " + id + ";";
    }

    /**
     * Give back an SQL string that will drop the column called 'age' in table 'competitor'.
     * @return An SQL string that will drop the column called 'age' in table 'competitor'.
     */
    public static String dropAgeColumn() {
        return "ALTER TABLE `CompetitionManager`.`competitor`\n" +
                "DROP COLUMN `age`;";
    }

    /**
     * Give back an SQL string that wil create an age column in table 'competitor' which contains the age in years.
     * @param dateOfToday A date string ISO-8601 format uuuu-MM-dd, like '2019-01-01'.
     * @return An SQL string that wil create an age column in table 'competitor' which contains the age in years.
     */
    static String createAgeColumn(String dateOfToday) {
        // String dateOfToday = "date('" + year + "-" + month + "-" + day + "')";
        //     * @param year The year as of the date of event.
        //     * @param month The month as of the date of event.
        //     * @param day The day of the month as of the date of event.
        return "ALTER TABLE `CompetitionManager`.`competitor`\n" +
                "ADD COLUMN `age` TINYINT unsigned\n" +
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
}
