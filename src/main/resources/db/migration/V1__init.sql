CREATE SEQUENCE expenses_seq
    START WITH 1
    INCREMENT BY 50;

CREATE SEQUENCE users_seq
    START WITH 1
    INCREMENT BY 50;

CREATE SEQUENCE user_expense_categories_seq
    START WITH 1
    INCREMENT BY 50;

CREATE SEQUENCE user_expense_sub_categories_seq
    START WITH 1
    INCREMENT BY 50;

CREATE SEQUENCE user_payment_methods_seq
    START WITH 1
    INCREMENT BY 50;


CREATE TABLE users (
    id BIGINT NOT NULL DEFAULT nextval('users_seq'),
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    identity_issuer VARCHAR(255) NOT NULL,
    identity_subject VARCHAR(255) NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_identity_issuer_subject
        UNIQUE (identity_issuer, identity_subject)
);


CREATE TABLE user_profiles (
    user_id BIGINT NOT NULL,

    CONSTRAINT pk_user_profiles PRIMARY KEY (user_id),
    CONSTRAINT fk_user_profiles_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
);


CREATE TABLE user_expense_categories (
    id BIGINT NOT NULL DEFAULT nextval('user_expense_categories_seq'),
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,

    CONSTRAINT pk_user_expense_categories PRIMARY KEY (id),
    CONSTRAINT uk_user_expense_categories_user_name
        UNIQUE (user_id, name),
    CONSTRAINT fk_user_expense_categories_user_profile
        FOREIGN KEY (user_id)
        REFERENCES user_profiles (user_id)
);


CREATE TABLE user_expense_sub_categories (
    id BIGINT NOT NULL DEFAULT nextval('user_expense_sub_categories_seq'),
    user_expense_category_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,

    CONSTRAINT pk_user_expense_sub_categories PRIMARY KEY (id),
    CONSTRAINT uk_user_expense_sub_categories_user_name_category
        UNIQUE (user_id, name, user_expense_category_id),
    CONSTRAINT fk_user_expense_sub_categories_category
        FOREIGN KEY (user_expense_category_id)
        REFERENCES user_expense_categories (id),
    CONSTRAINT fk_user_expense_sub_categories_user_profile
        FOREIGN KEY (user_id)
        REFERENCES user_profiles (user_id)
);


CREATE TABLE user_payment_methods (
    id BIGINT NOT NULL DEFAULT nextval('user_payment_methods_seq'),
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,

    CONSTRAINT pk_user_payment_methods PRIMARY KEY (id),
    CONSTRAINT uk_user_payment_methods_user_name
        UNIQUE (user_id, name),
    CONSTRAINT fk_user_payment_methods_user_profile
        FOREIGN KEY (user_id)
        REFERENCES user_profiles (user_id)
);


CREATE TABLE expenses (
    id BIGINT NOT NULL DEFAULT nextval('expenses_seq'),
    value DOUBLE PRECISION,
    date TIMESTAMP(6),
    user_expense_category_id BIGINT NOT NULL,
    user_expense_sub_category_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    user_payment_method_id BIGINT NOT NULL,
    comment VARCHAR(255),

    CONSTRAINT pk_expenses PRIMARY KEY (id),
    CONSTRAINT fk_expenses_category
        FOREIGN KEY (user_expense_category_id)
        REFERENCES user_expense_categories (id),
    CONSTRAINT fk_expenses_sub_category
        FOREIGN KEY (user_expense_sub_category_id)
        REFERENCES user_expense_sub_categories (id),
    CONSTRAINT fk_expenses_payment_method
        FOREIGN KEY (user_payment_method_id)
        REFERENCES user_payment_methods (id),
    CONSTRAINT fk_expenses_user_profile
        FOREIGN KEY (user_id)
        REFERENCES user_profiles (user_id)
);