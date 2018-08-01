module Api::V2
  class FolderApi < Grape::API
    namespace :folders do

      params do
        requires :id, type: String
        requires :type, type: String
        requires :token, type: String
      end

      get  do
        token = params[:token]
        if    params[:type] == 'googledrive'
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
          redirect_uri: "http://localhost:3000")


          @credentials.refresh_token = token
          @credentials.fetch_access_token!
          session = GoogleDrive::Session.from_credentials(@credentials)

          temp = session.file_by_id(params[:id])
          if temp.mime_type == 'application/vnd.google-apps.folder'
              @files = temp.files

              present :time, Time.now.to_s
              present :status, "success"
              present :message ,nil
              present :data ,@files, with: Api::Entities::GoogleDriveFolderEntity

          end
        elsif params[:type] == 'dropbox'
          dbx = Dropbox::Client.new(token)
          if params[:id] == 'root'
            files = dbx.list_folder("")
          else
            files = dbx.list_folder(params[:id])
          end

          present :time, Time.now.to_s
          present :status, "success"
          present :message ,nil
          present :data, files.entries, with: Api::Entities::DropboxFolderEntity
        elsif params[:type] == 'box'
          cloud = current_user.clouds.find_by_token(token)
          access_token = Boxr::refresh_tokens(token, client_id: "i9jieqavbpuutnbbrqdyeo44m0imegpk", client_secret: "4LjQ7N3toXIXVozyXOB21tBTcCo2KX6F").access_token
          refresh_token= Boxr::refresh_tokens(token, client_id: "i9jieqavbpuutnbbrqdyeo44m0imegpk", client_secret: "4LjQ7N3toXIXVozyXOB21tBTcCo2KX6F").refresh_token
          cloud.update(token:  refresh_token)

          client = Boxr::Client.new(access_token)

          if params[:id] == 'root'
            files = client.root_folder_items
          else
            files = client.folder_items(params[:id])
          end

          present :time, Time.now.to_s
          present :status, "success"
          present :message ,nil
          present :data, files.entries, with: Api::Entities::BoxFolderEntity
        end
      end




      params do
        requires :name, type: String
        requires :parent, type: String
        requires :type, type: String
        requires :token, type: String
      end

      post  do
        token = params[:token]
        if    params[:type] == 'googledrive'
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
          redirect_uri: "http://localhost:3000")

          @credentials.refresh_token = token
          token = @credentials.fetch_access_token!
          session = GoogleDrive::Session.from_credentials(@credentials)

          parent = session.file_by_id(params[:parent])
          parent.create_subcollection(params[:name])

          present :time, Time.now.to_s
          present :status, "success"
          present :message ,"Create Folder Successfully!"
          present :data, nil
        end
      end

    end
  end
end
