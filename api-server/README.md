# How to run

On Linux:

```console
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
python migrate.py up
uvicorn main:app --host 0.0.0.0 --port 8000
```

On Windows:

```console
python -m venv venv
venv\Scripts\Activate.ps1
pip install -r requirements.txt
python migrate.py up
uvicorn main:app --host 0.0.0.0 --port 8000
```
