module Api::V2
  class FileApi < Grape::API
    namespace :files do
      # GET FILE INFO
      params do
        requires :id, type: String
        requires :type, type: String
        requires :token, type: String
      end
      get do
        token = params[:token]
        if params[:type] == 'googledrive'
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
          @credentials.refresh_token = token
          @credentials.fetch_access_token!
          session = GoogleDrive::Session.from_credentials(@credentials)
          file = session.file_by_id(params[:id])

          dir = File.dirname("#{Rails.root}/public/files/#{current_user.id}/file")
          FileUtils.mkdir_p(dir) unless File.directory?(dir)
          file.download_to_file("#{dir}/#{file.title}")
          url = request.base_url + '/files/' + current_user.id.to_s + '/' + file.title

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
          dir = File.dirname("#{Rails.root}/public/files/#{current_user.id}/file")
          FileUtils.mkdir_p(dir) unless File.directory?(dir)
          title = dbx.get_metadata(params[:id]).name
          size = dbx.get_metadata(params[:id]).size
          created_time = dbx.get_metadata(params[:id]).client_modified
          Down.download(dbx.get_temporary_link(params[:id])[1], destination: "#{dir}/#{title}")
          url = request.base_url + '/files/' + current_user.id.to_s + '/' + title
          {
            time: Time.now.to_s,
            status: 'success',
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
          client = Boxr::Client.new(token)
          title = client.file(params[:id]).name
          created_time = client.file(params[:id]).content_created_at
          size = client.file(params[:id]).size


          dir = File.dirname("#{Rails.root}/public/files/#{current_user.id}/file")
          FileUtils.mkdir_p(dir) unless File.directory?(dir)
          Down.download(client.download_url(params[:id]), destination: "#{dir}/#{title}")
          url = request.base_url + '/files/' + current_user.id.to_s + '/' + title
          {
            time: Time.now.to_s,
            status: 'success',
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
        parent = params[:id]
        token = params[:token]
        dir = params[:file][:tempfile].path
        title = params[:file][:filename].to_s
        type = params[:file][:type]

        # UPLOAD GOOGLE DRIVE
        if params[:type] == 'googledrive'
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
          @credentials.refresh_token = token
          @credentials.fetch_access_token!
          session = GoogleDrive::Session.from_credentials(@credentials)
          parent = session.file_by_id(parent)
          parent.upload_from_file(dir, title, convert: false)

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Upload Successfully!'
          present :data, nil

        # UPLOAD DROPBOX
        elsif params[:type] == 'dropbox'
          dbx = Dropbox::Client.new(token)
          if params[:id] == 'root'
            dbx.upload('/' + title, File.open(dir, 'r'))
          else
            dbx.upload(params[:id] + '/' + title, File.open(dir, 'r'))
          end

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Upload Successfully!'
          present :data, nil

        elsif params[:type] == 'box'
          client = Boxr::Client.new(token)
          client.upload_file(dir, parent, name: title)
          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Upload Successfully!'
          present :data, nil
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
        if params[:type] == 'googledrive'
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
            @credentials.refresh_token = token
            @credentials.fetch_access_token!
            session = GoogleDrive::Session.from_credentials(@credentials)

            file = session.file_by_id(params[:id])
            file.rename(name)

            present :time, Time.now.to_s
            present :status, 'success'
            present :message, 'Update Successfully!'
            present :data, nil
        # RENAME A FIELD IN DROPBOX
        elsif params[:type] == 'dropbox'
            dbx = Dropbox::Client.new(token)
            parent = File.dirname(params[:id])

            dbx.move(params[:id], parent.to_s + name.to_s)

            present :time, Time.now.to_s
            present :status, 'success'
            present :message, 'Update Successfully!'
            present :data, nil
        elsif params[:type] == 'box'
            client = Boxr::Client.new(token)
            client.update_file(params[:id], name: name)

            present :time, Time.now.to_s
            present :status, 'success'
            present :message, 'Delete Successfully!'
            present :data, nil
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
        if params[:type] == 'googledrive'
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
          @credentials.refresh_token = token
          @credentials.fetch_access_token!
          session = GoogleDrive::Session.from_credentials(@credentials)

          file = session.file_by_id(params[:id])
          file.delete(true)

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Delete Successfully!'
          present :data, nil

        # DELETE A FIELD IN DROPBOX
        elsif params[:type] == 'dropbox'
          dbx = Dropbox::Client.new(token)
          dbx.delete(params[:id])

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Delete Successfully!'
          present :data, nil

        # DELETE A FIELD IN BOX
        elsif params[:type] == 'box'
            client = Boxr::Client.new(token)
            client.delete_file(params[:id])

            present :time, Time.now.to_s
            present :status, 'success'
            present :message, 'Delete Successfully!'
            present :data, nil
        end
      end
    end
  end
end
