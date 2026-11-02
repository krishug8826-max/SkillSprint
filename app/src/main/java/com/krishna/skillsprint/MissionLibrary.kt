package com.krishna.skillsprint

data class MissionDefinition(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val points: Int
)

object MissionLibrary {


    val allMissions = listOf(

        // STUDY AND LEARNING
        MissionDefinition("M01", "Learn 10 English Words", "Learn ten new English words and their meanings.", "LEARNING", 50),
        MissionDefinition("M02", "Grammar Practice", "Complete one English grammar topic.", "LEARNING", 50),
        MissionDefinition("M03", "Focused Study", "Study without distractions for 30 minutes.", "LEARNING", 100),
        MissionDefinition("M04", "Practice Questions", "Solve five practice questions.", "LEARNING", 100),
        MissionDefinition("M05", "Chapter Summary", "Write a short summary of a chapter.", "LEARNING", 100),
        MissionDefinition("M06", "Teach a Concept", "Explain something you learned to a friend.", "LEARNING", 100),
        MissionDefinition("M07", "Educational Video", "Watch an educational video and note three points.", "LEARNING", 75),
        MissionDefinition("M08", "Revise a Topic", "Revise a topic you find difficult.", "LEARNING", 75),

        // CODING AND TECHNOLOGY
        MissionDefinition("M09", "Solve a Coding Problem", "Solve one programming problem.", "CODING", 50),
        MissionDefinition("M10", "Learn Java or Kotlin", "Learn one new Java or Kotlin concept.", "CODING", 75),
        MissionDefinition("M11", "Build a Small Program", "Create a small working program.", "CODING", 100),
        MissionDefinition("M12", "Fix a Bug", "Find and fix one bug in a project.", "CODING", 150),
        MissionDefinition("M13", "Practice Git", "Learn and use one Git command.", "CODING", 50),
        MissionDefinition("M14", "Improve Your App", "Improve one part of your app UI.", "CODING", 100),
        MissionDefinition("M15", "Practice SQL", "Write and test one SQL query.", "CODING", 100),
        MissionDefinition("M16", "Coding Focus", "Practise coding for 30 minutes.", "CODING", 100),

        // ENGLISH AND PERSONALITY
        MissionDefinition("M17", "Speak English", "Speak in English continuously for two minutes.", "COMMUNICATION", 100),
        MissionDefinition("M18", "Mirror Introduction", "Introduce yourself in English in front of a mirror.", "COMMUNICATION", 50),
        MissionDefinition("M19", "Write a Paragraph", "Write a short paragraph in English.", "COMMUNICATION", 75),
        MissionDefinition("M20", "Interview Practice", "Practise answering five interview questions.", "COMMUNICATION", 150),
        MissionDefinition("M21", "News Summary", "Read an English news article and summarize it.", "COMMUNICATION", 100),
        MissionDefinition("M22", "Know Your Strengths", "Write down three personal strengths.", "PERSONAL_GROWTH", 50),
        MissionDefinition("M23", "Weekly Goals", "Set three goals for the next seven days.", "PERSONAL_GROWTH", 50),

        // TRUTH AND DARE
        MissionDefinition("M24", "Truth: Funny Memory", "Recall a funny memory you are comfortable sharing.", "TRUTH_DARE", 50),
        MissionDefinition("M25", "Truth: Your Strength", "Identify a strength you are proud of.", "TRUTH_DARE", 50),
        MissionDefinition("M26", "Truth: Hidden Talent", "Think about a talent you would like to show.", "TRUTH_DARE", 75),
        MissionDefinition("M27", "Truth: Dream Skill", "Name a skill you dream of learning.", "TRUTH_DARE", 50),
        MissionDefinition("M28", "Dare: Funny Voice", "Speak in a funny voice for 30 seconds.", "TRUTH_DARE", 75),
        MissionDefinition("M29", "Dare: Tongue Twister", "Say a tongue twister three times.", "TRUTH_DARE", 75),
        MissionDefinition("M30", "Dare: Movie Style", "Introduce yourself in a dramatic movie style.", "TRUTH_DARE", 75),
        MissionDefinition("M31", "Dare: Favourite Topic", "Speak for one minute about your favourite topic.", "TRUTH_DARE", 100),

        // FRIENDSHIP AND SOCIAL
        MissionDefinition("M32", "Give a Compliment", "Give a genuine compliment to a friend.", "SOCIAL", 50),
        MissionDefinition("M33", "Motivate a Friend", "Encourage a friend to work towards a goal.", "SOCIAL", 50),
        MissionDefinition("M34", "Learning Discussion", "Discuss an educational topic with a friend.", "SOCIAL", 75),
        MissionDefinition("M35", "Meet Someone New", "Introduce yourself respectfully to someone new.", "SOCIAL", 75),
        MissionDefinition("M36", "English Conversation", "Have a five-minute English conversation.", "SOCIAL", 100),
        MissionDefinition("M37", "Share a Useful Skill", "Teach someone a useful tip or skill.", "SOCIAL", 100),
        MissionDefinition("M38", "Appreciation Message", "Send someone a kind appreciation message.", "SOCIAL", 50),

        // FITNESS AND HEALTHY HABITS
        MissionDefinition("M39", "Take a Walk", "Walk for 15 minutes at a comfortable pace.", "FITNESS", 50),
        MissionDefinition("M40", "Stretch Break", "Do gentle stretching for five minutes.", "FITNESS", 50),
        MissionDefinition("M41", "Screen Break", "Take a screen break during your study session.", "FITNESS", 50),
        MissionDefinition("M42", "Organize Workspace", "Organize your desk or study area.", "LIFESTYLE", 50),
        MissionDefinition("M43", "Plan Tomorrow", "Plan your tasks for tomorrow.", "LIFESTYLE", 50),

        // REAL LIFE AND PERSONAL GROWTH
        MissionDefinition("M44", "List Today's Tasks", "Write down three important tasks for today.", "PERSONAL_GROWTH", 50),
        MissionDefinition("M45", "Finish a Pending Task", "Complete one pending task.", "PERSONAL_GROWTH", 100),
        MissionDefinition("M46", "Improve a Habit", "Take one practical step towards improving a habit.", "PERSONAL_GROWTH", 100),
        MissionDefinition("M47", "Today's Achievement", "Write down one achievement from today.", "PERSONAL_GROWTH", 50),
        MissionDefinition("M48", "Social Media Break", "Take a 30-minute break from social media.", "LIFESTYLE", 75),
        MissionDefinition("M49", "Help at Home", "Help a family member with a task.", "LIFESTYLE", 50),
        MissionDefinition("M50", "Set a Future Goal", "Choose one future goal and write its next step.", "PERSONAL_GROWTH", 75)
    )

    fun findById(id: String): MissionDefinition? {
        return allMissions.firstOrNull { it.id == id }
    }


}
