CREATE TABLE accounts (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(20) NOT NULL,
    pass VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY username_unique (username)
)  ENGINE=INNODB;

CREATE TABLE books (
    id VARCHAR(10) NOT NULL,
    year_ YEAR NOT NULL,
    title VARCHAR(40) NOT NULL,
    author VARCHAR(20) NOT NULL,
    description_ VARCHAR(100),
    quantity INT NOT NULL,
    PRIMARY KEY (id)
)  ENGINE=INNODB;

CREATE TABLE wishlists (
    id INT NOT NULL AUTO_INCREMENT,
    id_book VARCHAR(10) NOT NULL,
    username_account VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_wishlist_books
		FOREIGN KEY (id_book) REFERENCES book (id),
	CONSTRAINT fk_wishlist_accounts
		FOREIGN KEY (username_account) REFERENCES accounts (username)
)  ENGINE=INNODB;

CREATE TABLE borrowing_history (
    id INT NOT NULL AUTO_INCREMENT,
    id_book VARCHAR(10) NOT NULL,
    username_account VARCHAR(20) NOT NULL,
    time_start TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    time_end TIMESTAMP,
    borrowing_status ENUM ("Borrowing","Returned") NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_borrowing_history_books 
		FOREIGN KEY (id_book) REFERENCES books (id),
	CONSTRAINT fk_borrowing_history_accounts
		FOREIGN KEY (username_account) REFERENCES accounts (username)
)  ENGINE=INNODB;