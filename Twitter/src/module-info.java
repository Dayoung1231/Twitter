module twitterClone {

    // 내부에서 사용하는 패키지들 선언
    exports main;         // 메인 실행 패키지
    exports model;        // 모델 클래스 (UserModel, PostModel 등)
    exports database;     // 데이터베이스 연결 패키지

    // 필요한 모듈 선언
    requires java.sql;    // JDBC를 사용하기 위해 java.sql 모듈 필요
}
