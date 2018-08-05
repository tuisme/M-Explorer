class HardWorker
  include Sidekiq::Worker

  def perform()
    success = 0
    error = 0
    Cloud.all.each do |cloud|
      if cloud.cloud_type == 'googledrive'
        @credentials = Google::Auth::UserRefreshCredentials.new(
          client_id: '389228917380-ek9t84cthihvi8u4apphlojk3knd5geu.apps.googleusercontent.com',
          client_secret: 'zhKkS-8vI_RNqReXOjAx4c5r',
          scope: [
            'https://www.googleapis.com/auth/drive'
          ],
          additional_parameters: {
            'access_type' => 'offline',
            'prompt' => 'consent'
          },
          redirect_uri: 'http://localhost:3000')
        @credentials.refresh_token = cloud.refresh_token
        access_token = @credentials.fetch_access_token!['access_token']
        if cloud.update(access_token: access_token, updated_at: Time.now)
          success += 1
        else
          error += 1
        end
      elsif cloud.cloud_type == 'box'
        access_token = Boxr.refresh_tokens(cloud.refresh_token, client_id: 'i9jieqavbpuutnbbrqdyeo44m0imegpk', client_secret: '4LjQ7N3toXIXVozyXOB21tBTcCo2KX6F').access_token
        refresh_token = Boxr.refresh_tokens(cloud.refresh_token, client_id: 'i9jieqavbpuutnbbrqdyeo44m0imegpk', client_secret: '4LjQ7N3toXIXVozyXOB21tBTcCo2KX6F').refresh_token
        if cloud.update(access_token: access_token, refresh_token: refresh_token, updated_at: Time.now)
          success += 1
        else
          error += 1
        end
      end
    end
    puts 'Success: ' + success.to_s + ' | Error: ' + error.to_s
  end
end
