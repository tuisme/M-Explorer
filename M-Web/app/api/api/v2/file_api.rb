module Api::V2
  class FileApi < Grape::API
    namespace :files do

      #GET FILE INFO
      params do
        requires :id, type: String
        requires :type, type: String
        requires :token, type: String
      end
      get do
        token = params[:token]
        if params[:type] == 'googledrive'
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
          file = session.file_by_id(params[:id])

          dir = File.dirname("#{Rails.root}/public/files/#{current_user.id.to_s}/file")
          FileUtils.mkdir_p(dir) unless File.directory?(dir)
          file.download_to_file("#{dir}/#{file.title}")
          url = request.base_url + "/files/" + current_user.id.to_s + "/" + file.title

          {
            time: Time.now.to_s,
            status: 'success',
            message: nil,
            data: {
              id: file.id,
              name: file.name,
              url: url,
              created_time: file.created_time,
              mime_type: file.mime_type,
              size: file.size
            }
          }

        elsif params[:type] == 'dropbox'
          dbx = Dropbox::Client.new(token)
          dir = File.dirname("#{Rails.root}/public/files/#{current_user.id.to_s}/file")
          FileUtils.mkdir_p(dir) unless File.directory?(dir)
          title = dbx.get_metadata(params[:id]).name
          size = dbx.get_metadata(params[:id]).size
          created_time = dbx.get_metadata(params[:id]).client_modified
          Down.download(dbx.get_temporary_link(params[:id])[1], destination: "#{dir}/#{title}")
          url = request.base_url + "/files/" + current_user.id.to_s + "/" + title
          {
            time: Time.now.to_s,
            status: "success",
            message: nil,
            data: {
              id: params[:id],
              name: title,
              url: url,
              created_time: created_time,
              mime_type: nil,
              size: size
            }
          }
        elsif params[:type] == 'box'
          cloud = current_user.clouds.find_by(access_token: token)
          access_token = Boxr::refresh_tokens(token, client_id: "i9jieqavbpuutnbbrqdyeo44m0imegpk", client_secret: "4LjQ7N3toXIXVozyXOB21tBTcCo2KX6F").access_token
          refresh_token= Boxr::refresh_tokens(token, client_id: "i9jieqavbpuutnbbrqdyeo44m0imegpk", client_secret: "4LjQ7N3toXIXVozyXOB21tBTcCo2KX6F").refresh_token
          cloud.update(token:  refresh_token)

          client = Boxr::Client.new(access_token)
          title = client.file(params[:id]).name
          created_time = client.file(params[:id]).content_created_at
          size = client.file(params[:id]).size

          dir = File.dirname("#{Rails.root}/public/files/#{current_user.id.to_s}/file")
          FileUtils.mkdir_p(dir) unless File.directory?(dir)
          Down.download(client.download_url(params[:id]), destination: "#{dir}/#{title}")
          url = request.base_url + "/files/" + current_user.id.to_s + "/" + title
          {
            time: Time.now.to_s,
            status: "success",
            message: nil,
            data: {
              id: params[:id],
              name: title,
              url: url,
              created_time: created_time,
              mime_type: nil,
              size: size
            }
          }
        end
      end


      # UPLOAD A FILE TO CLOUD
      params do
        requires :id, type: String
        requires :file, type: File
        requires :type, type: String
        requires :token, type: String
      end
      post do
        token = params[:token]
        dir = params[:file][:tempfile].path
        title = params[:file][:filename].to_s
        type = params[:file][:type]

        # UPLOAD GOOGLE DRIVE
        if params[:type] == "googledrive"
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
          parent = session.file_by_id(params[:id])
          parent.upload_from_file(dir,title,convert: false)

          present :time, Time.now.to_s
          present :status, "success"
          present :message ,"Upload Successfully!"
          present :data, nil

       # UPLOAD DROPBOX
        elsif params[:type] == "dropbox"
          dbx = Dropbox::Client.new(token)
          if params[:id] == 'root'
            dbx.upload("/sJPG.jpg" ,"/home/kyle/Project/M_Explorer_1/public/files/1/JPG.jpg")
          else
            dbx.upload(params[:id] + '/' + title,dir)
          end

          {
            a: true
          }

        end
      end


      # RENAME A FIELD IN CLOUD
      params do
        requires :id, type: String
        requires :name, type: String
        requires :type, type: String
        requires :token, type: String
      end
      put do
        token = params[:token]
        name = params[:name]


        # RENAME A FIELD IN GOOGLE DRIVE
        if params[:type] == "googledrive"
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

          file = session.file_by_id(params[:id])
          file.rename(name)

          present :time, Time.now.to_s
          present :status, "success"
          present :message ,"Update Successfully!"
          present :data, nil
       # RENAME A FIELD IN DROPBOX
        elsif params[:type] == "dropbox"
          dbx = Dropbox::Client.new(token)
          if params[:fid] == 'root'
            dbx.upload("/sJPG.jpg" ,"/home/kyle/Project/M_Explorer_1/public/files/1/JPG.jpg")
          else
            dbx.upload(params[:fid] + '/' + title,dir)
          end

          {
            a: true
          }

        end
      end



      # DELETE A FIELD IN CLOUD
      params do
        requires :id, type: String
        requires :type, type: String
        requires :token, type: String
      end
      delete do
        token = params[:token]

        # DELETE A FIELD IN GOOGLE DRIVE
        if params[:type] == "googledrive"
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

          file = session.file_by_id(params[:id])
          file.delete(true)

          present :time, Time.now.to_s
          present :status, "success"
          present :message ,"Delete Successfully!"
          present :data, nil

       # RENAME A FIELD IN DROPBOX
        elsif params[:type] == "dropbox"
          dbx = Dropbox::Client.new(token)
          if params[:fid] == 'root'
            dbx.upload("/sJPG.jpg" ,"/home/kyle/Project/M_Explorer_1/public/files/1/JPG.jpg")
          else
            dbx.upload(params[:fid] + '/' + title,dir)
          end

          {
            a: true
          }

        end
      end
    end
  end
end
