DELETE FROM "exercises" WHERE "gen_id" = 2;

INSERT INTO "exercises"
    ("gen_id", "type", "data")
VALUES
    (
        1,
        103,
        '{
            "left": [
                "artiste",
                "assistant",
                "chef",
                "confectioner",
                "director",
                "minister",
                "recordist",
                "speaker",
                "valet",
                "visitator"
            ],
            "right": [
                "circus",
                "office",
                "kitchen",
                "cake shop",
                "film location",
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
        1,
        103,
        '{
            "left": [
                "casque",
                "flask",
                "compote",
                "erate",
                "locator",
                "cistern"
            ],
            "right": [
                "for protecting knights'' heads",
                "for transporting bottles",
                "for serving food",
                "for indicating things",
                "for removing underwater mines",
                "for closing a bottle"
            ]
        }'
    ),
    (
        1,
        103,
        '{
            "left": [
                "ambo",
                "ambulatory",
                "billet",
                "chalet",
                "lyceum",
                "perron",
                "resort",
                "sanctuary",
                "souterrain"
            ],
            "right": [
                "pulpit in Greek or Balkan churches",
                "walking area in a cloister",
                "soldiers'' lodging place",
                "Alpine house",
                "place for holding lectures",
                "outdoor stairway",
                "popular haunt",
                "wildlife reserve",
                "underground passage",
                "basement accommodation",
                "medieval hall of residence"
            ]
        }'
    );
