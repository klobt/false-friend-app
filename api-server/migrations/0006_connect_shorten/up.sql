DELETE FROM "exercises" WHERE "gen_id" = 1 AND "type" = 103;

INSERT INTO "exercises"
    ("gen_id", "type", "data")
VALUES
    (
        2,
        103,
        '{
            "left": [
                "minister",
                "recordist",
                "speaker",
                "valet",
                "visitator"
            ],
            "right": [
                "Parliament",
                "sound studio",
                "church",
                "hotel car park",
                "monasteries",
                "clothes shop"
            ]
        }'
    ),
    (
        2,
        103,
        '{
            "left": [
                "flask",
                "compote",
                "erate",
                "locator",
                "cistern"
            ],
            "right": [
                "for transporting bottles",
                "for serving food",
                "for indicating things",
                "for removing underwater mines",
                "for closing a bottle",
                "for protecting knights'' heads"
            ]
        }'
    ),
    (
        2,
        103,
        '{
            "left": [
                "ambo",
                "ambulatory",
                "billet",
                "chalet",
                "lyceum"
            ],
            "right": [
                "pulpit in Greek or Balkan churches",
                "walking area in a cloister",
                "soldiers'' lodging place",
                "Alpine house",
                "place for holding lectures",
                "medieval hall of residence"
            ]
        }'
    ),
    (
        2,
        103,
        '{
            "left": [
                "perron",
                "resort",
                "sanctuary",
                "souterrain"
            ],
            "right": [
                "outdoor stairway",
                "popular haunt",
                "wildlife reserve",
                "underground passage",
                "basement accommodation"
            ]
        }'
    );
