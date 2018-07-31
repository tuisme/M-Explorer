#LOG
set :output, "log/cron_log.log"

#COMMAND
every 1.minutes do
    rake "access_token"
end
