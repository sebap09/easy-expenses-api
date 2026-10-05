WITH user_id_cte AS (
INSERT INTO users (identity_issuer, identity_subject)
VALUES (
    'http://keycloak:8080/realms/my-realm',
    'f759323e-5b47-44a6-8d0f-a8b5563cccd1'
    )
    RETURNING id AS user_id
    ),
    user_payment_cte AS (
INSERT INTO user_payment_methods (user_id, name)
SELECT
    user_id_cte.user_id,
    'CARD'
FROM user_id_cte
    ),
    first_category_cte AS(
INSERT INTO user_expense_categories (user_id, name)
SELECT
    user_id_cte.user_id,
    'ZAKUPY'
FROM user_id_cte
    RETURNING
    id AS first_category_id,
    user_id
    ),
    second_category_cte AS (
INSERT INTO user_expense_categories (user_id, name)
SELECT
    user_id_cte.user_id,
    'TRANSPORT'
FROM user_id_cte
    RETURNING
    id AS second_category_id,
    user_id
    ),
    first_category_first_subcategory_cte AS (
INSERT INTO user_expense_sub_categories (user_expense_category_id, user_id, name)
SELECT
    first_category_cte.first_category_id,
    first_category_cte.user_id,
    'JEDZENIE/KOSMETYKI/CHEMIA/INNE'
FROM first_category_cte
    LEFT JOIN user_id_cte
ON first_category_cte.user_id = user_id_cte.user_id
    ),
    first_category_second_subcategory_cte AS (
INSERT INTO user_expense_sub_categories (user_expense_category_id, user_id, name)
SELECT
    first_category_cte.first_category_id,
    first_category_cte.user_id,
    'APTEKA'
FROM first_category_cte
    LEFT JOIN user_id_cte
ON first_category_cte.user_id = user_id_cte.user_id
    ),
    second_category_first_subcategory_cte AS (
INSERT INTO user_expense_sub_categories (user_expense_category_id, user_id, name)
SELECT
    second_category_id,
    second_category_cte.user_id,
    'PARKING'
FROM second_category_cte
    LEFT JOIN user_id_cte
ON second_category_cte.user_id = user_id_cte.user_id
    ),
    second_category_second_subcategory_cte AS (
INSERT INTO user_expense_sub_categories (user_expense_category_id, user_id, name)
SELECT
    second_category_id,
    second_category_cte.user_id,
    'PALIWO'
FROM second_category_cte
    LEFT JOIN user_id_cte
ON second_category_cte.user_id = user_id_cte.user_id
    )
INSERT INTO user_profiles
SELECT
    user_id_cte.user_id
FROM user_id_cte;