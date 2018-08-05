module Api::V2
  class CloudApi < Grape::API
    namespace :clouds do
      get do
        present :time, Time.now.to_s
        present :status, 'success'
        present :message, nil
        present :data, current_user.clouds, with: Api::Entities::CloudEntity
      end

      params do
        requires :code, type: String
        requires :provider, type: String
        requires :name, type: String
      end
      post do
        name = params[:name]
        code = params[:code]
        if params[:provider] == 'googledrive'
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
            redirect_uri: 'http://localhost:3000/api/v2/clouds')

          @credentials.code = code
          access_token = @credentials.fetch_access_token!['access_token']
          session = GoogleDrive::Session.from_credentials(@credentials)

          root = session.root_collection.id.to_s
          refresh_token = @credentials.refresh_token
          type = 'googledrive'

          @result = HTTParty.get('https://www.googleapis.com/drive/v2/about?access_token=' + access_token)
          parsed_json = JSON.parse(@result.body)

          used = parsed_json['quotaBytesUsed'].to_i
          allocated = if parsed_json['quotaType'] == 'UNLIMITED'
                        0
                      else
                        parsed_json['quotaBytesTotal'].to_i
                      end

        elsif params[:provider] == 'dropbox'
          type = 'dropbox'
          root = 'root'
          dbx = Dropbox::Client.new(code)
          used = dbx.get_space_usage.used
          allocated = dbx.get_space_usage.allocated
          access_token = code
        # elsif params[:provider] == 'onedrive'
        #   type = 'onedrive'
        #   root = ""
        #   used = 0
        #   allocated = 0
        elsif params[:provider] == 'box'
          type = 'box'
          access_token = Boxr.refresh_tokens(code, client_id: ENV['box_client_id'], client_secret: ENV['box_client_secret']).access_token
          refresh_token = Boxr.refresh_tokens(code, client_id: ENV['box_client_id'], client_secret: ENV['box_client_secret']).refresh_token
          client = Boxr::Client.new(access_token)
          root = Boxr::ROOT
          used = client.me.space_used
          allocated = client.me.space_amount
        else
          {
            time: Time.now.to_s,
            status: 'error',
            message: 'Add Failed!',
            data: nil
          }
        end

        if current_user.clouds.create!(cloud_root: root, cloud_name: name, cloud_type: type, refresh_token:  refresh_token, access_token: access_token, used: used, allocated: allocated)
          {
            time: Time.now.to_s,
            status: 'success',
            message: 'Add cloud successful!',
            data: current_user.clouds
          }
        else
          {
            time: Time.now.to_s,
            status: 'error',
            message: 'Failed!',
            data: current_user.clouds
          }
        end
      end

      params do
        requires :name, type: String
      end

      put ':id' do
        cloud = current_user.clouds.find_by_id(params[:id])
        if cloud.update(cloud_name: params[:name])
          {
            time: Time.now.to_s,
            status: 'success',
            message: nil,
            data: current_user.clouds
          }
        else
          {
            time: Time.now.to_s,
            status: 'error',
            message: 'Error',
            data: current_user.clouds
          }
        end
      end

      delete ':id' do
        cloud = current_user.clouds.find_by_id(params[:id])
        if cloud.destroy
          {
            time: Time.now.to_s,
            status: 'success',
            message: nil,
            data: nil
          }
        end
      end
    end
  end
end
