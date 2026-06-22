# False Friend App
An application supporting the study of foreign languages with focus on homonyms and linguistic interference.

## Requirements

1. Android Studio ([how to install](https://developer.android.com/studio/install))
2. git
3. python/pip

## How to run:

1. Clone the repository

```shell
git clone 'ssh://git@github.com:klobt/false-friend-app.git'
cd false-friend-app
```

2. Run the API server (you may venv inside the `api-server` directory if you prefer)

```shell
cd api-server
pip install -r requirements.txt
python migrate.py up
uvicorn main:app --host 0.0.0.0 --port 8000
```

3. Open the `false-friend-app` project inside Android Studio

4. Run the project on a connected device or an emulated one

5. The application should be ready use and connect with the server running on `localhost`
