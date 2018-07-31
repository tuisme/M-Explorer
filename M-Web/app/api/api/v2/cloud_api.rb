module Api::V2
  class CloudApi < Grape::API
    namespace :clouds do

      get do
        {
          time: Time.now.to_s,
          status: 'success',
          message: nil,
          data: current_user.clouds
        }
      end

      params do
        requires :code, type: String
        requires :provider, type: String
        requires :cname, type: String
      end

      post do
        cname = params[:cname]
        token = params[:code]

        if params[:provider] == 'googledrive'
          @credentials = Google::Auth::UserRefreshCredentials.new(
          client_id: "389228917380-ek9t84cthihvi8u4apphlojk3knd5geu.apps.googleusercontent.com",
          client_secret: "zhKkS-8vI_RNqReXOjAx4c5r",
          scope: [
            "https://www.googleapis.com/auth/drive"
          ],
          additional_parameters: {
            "access_type" => "offline",
            "prompt" => "consent"
          },
          redirect_uri: "http://localhost:3000/api/v2/clouds")

          @credentials.code = token
          access_temp = @credentials.fetch_access_token!
          session = GoogleDrive::Session.from_credentials(@credentials)

          root = session.root_collection.id.to_s
          token = @credentials.refresh_token
          ctype = 'googledrive'

          @result = HTTParty.get('https://www.googleapis.com/drive/v2/about?access_token='+ access_temp['access_token'])
          parsed_json = JSON.parse(@result.body)

          used = parsed_json['quotaBytesUsed'].to_i
          unused = parsed_json['quotaBytesTotal'].to_i - parsed_json['quotaBytesUsed'].to_i

        elsif params[:provider] == 'dropbox'
          ctype = 'dropbox'
          root = "root"
          dbx = Dropbox::Client.new(token)
          used = dbx.get_space_usage.used
          unused = dbx.get_space_usage.allocated - dbx.get_space_usage.used
        elsif params[:provider] == 'onedrive'
          ctype = 'onedrive'
          root = ""
          used = 0
          unused = 0
        elsif params[:provider] == 'box'
          ctype = 'box'
          root = "root"
          access_token = Boxr::refresh_tokens(token, client_id: "i9jieqavbpuutnbbrqdyeo44m0imegpk", client_secret: "4LjQ7N3toXIXVozyXOB21tBTcCo2KX6F").access_token
          token= Boxr::refresh_tokens(token, client_id: "i9jieqavbpuutnbbrqdyeo44m0imegpk", client_secret: "4LjQ7N3toXIXVozyXOB21tBTcCo2KX6F").refresh_token

          client = Boxr::Client.new(access_token)
          used = client.me.space_used
          unused = client.me.space_amount - client.me.space_used
        else
          {
            time: Time.now.to_s,
            status: 'error',
            message: 'Add Failed!',
            data: nil
          }
        end




        if current_user.clouds.create!(croot: root,cname: cname, ctype: ctype,ctoken:  token, used: used, unused: unused)
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
        requires :cname, type: String
      end

      put ':id' do
        cloud = current_user.clouds.find_by_id(params[:id])
        if cloud.update(declared(params))
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
            message: "Error",
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
