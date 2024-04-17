import pandas as pd
from timestamp import *
from ml_rf_1 import add_timestamp_columns, preprocess

data = pd.read_csv("../data/tmp_data.csv")

preprocess(data)
print(data)

