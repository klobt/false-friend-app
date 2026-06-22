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

2. Enter the `api-server` directory

```shell
cd api-server
pip install -r requirements.txt
python migrate.py up
uvicorn main:app --host 0.0.0.0 --port 8000
```

3. You may enter `venv` if you prefer (for instance if your system restricts installing python packages)

```shell
python -m venv venv
source venv/bin/activate
```

4. Run the API server

```shell
pip install -r requirements.txt
python migrate.py up
uvicorn main:app --host 0.0.0.0 --port 8000
```

5. Open the `false-friend-app` project inside Android Studio

6. Run the project on a connected device or an emulated one

7. The application should be ready use and connect with the server running on `localhost`
