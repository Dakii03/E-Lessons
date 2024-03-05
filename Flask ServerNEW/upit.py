import datetime

# Get the current time as datetime
current_datetime = datetime.datetime.now()

# Add one hour to the current time
current_time = (datetime.datetime.now() + datetime.timedelta(hours=1)).time().strftime('%H:%M')

print("Current time:",current_time )


