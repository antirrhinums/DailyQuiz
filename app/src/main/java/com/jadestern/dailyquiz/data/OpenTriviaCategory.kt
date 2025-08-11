package com.jadestern.dailyquiz.data

enum class OpenTriviaCategory(val id: Int?, val displayName: String, val apiName: String) {
    ANY(null, "Любая", "General Knowledge"),
    GENERAL_KNOWLEDGE(9, "Общие знания", "General Knowledge"),
    ENTERTAINMENT_BOOKS(10, "Книги", "Entertainment: Books"),
    ENTERTAINMENT_FILM(11, "Фильмы", "Entertainment: Film"),
    ENTERTAINMENT_MUSIC(12, "Музыка", "Entertainment: Music"),
    ENTERTAINMENT_MUSICALS_THEATRE(13, "Мюзиклы и театр", "Entertainment: Musicals & Theatres"),
    ENTERTAINMENT_TELEVISION(14, "Телевидение", "Entertainment: Television"),
    ENTERTAINMENT_VIDEO_GAMES(15, "Видеоигры", "Entertainment: Video Games"),
    ENTERTAINMENT_BOARD_GAMES(16, "Настольные игры", "Entertainment: Board Games"),
    SCIENCE_NATURE(17, "Наука и природа", "Science & Nature"),
    SCIENCE_COMPUTERS(18, "Компьютеры", "Science: Computers"),
    SCIENCE_MATHEMATICS(19, "Математика", "Science: Mathematics"),
    MYTHOLOGY(20, "Мифология", "Mythology"),
    SPORTS(21, "Спорт", "Sports"),
    GEOGRAPHY(22, "География", "Geography"),
    HISTORY(23, "История", "History"),
    POLITICS(24, "Политика", "Politics"),
    ART(25, "Искусство", "Art"),
    CELEBRITIES(26, "Знаменитости", "Celebrities"),
    ANIMALS(27, "Животные", "Animals"),
    VEHICLES(28, "Транспорт", "Vehicles"),
    ENTERTAINMENT_COMICS(29, "Комиксы",  "Entertainment: Comics"),
    SCIENCE_GADGETS(30, "Гаджеты", "Science: Gadgets"),
    ENTERTAINMENT_ANIME_MANGA(31, "Аниме и манга", "Entertainment: Japanese Anime & Manga"),
    ENTERTAINMENT_CARTOON_ANIMATION(32, "Мультфильмы", "Entertainment: Cartoon & Animations")
}